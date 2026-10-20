package com.yapp.plus.data.auth

import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.domain.auth.GoogleLoginCredential
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseAuthDataSourceTest {
    @Test
    fun `Google ID token and raw nonce are exchanged and only session tokens are persisted`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource()
            withSource(storage, handler = { request ->
                assertEquals(HttpMethod.Post, request.method)
                assertEquals("/auth/v1/token", request.url.encodedPath)
                assertEquals("id_token", request.url.parameters["grant_type"])
                assertEquals("sb_publishable_test", request.headers["apikey"])
                assertNull(request.headers[HttpHeaders.Authorization])
                val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                assertEquals("google", body.getValue("provider").jsonPrimitive.content)
                assertEquals("google-secret", body.getValue("id_token").jsonPrimitive.content)
                assertEquals("raw-nonce-secret", body.getValue("nonce").jsonPrimitive.content)
                respond(tokenResponse())
            }) { source ->
                val session = AuthRepositoryImpl(source).signInWithGoogle(credential())
                assertEquals("user", session.userId)
                assertEquals(tokens(), storage.tokens)
                assertFalse(storage.tokens.toString().contains("access-secret"))
                assertFalse(credential().toString().contains("raw-nonce-secret"))
            }
        }

    @Test
    fun `empty storage restores no session without making a request`() =
        runTest {
            withSource(handler = { error("Unexpected network request") }) { source ->
                assertNull(AuthRepositoryImpl(source).fetchSession())
            }
        }

    @Test
    fun `missing client key fails explicitly before sending a credential`() =
        runTest {
            withSource(publishableKey = "", handler = { error("Unexpected request") }) { source ->
                val failure =
                    runCatching { source.signInWithGoogle(credential()) }.exceptionOrNull()
                assertRedacted(failure)
                assertTrue(failure?.message?.contains("supabase.publishableKey") == true)
            }
        }

    @Test
    fun `stored session is restored using the authenticated user endpoint`() =
        runTest {
            withSource(FakeAuthTokenLocalDataSource(tokens()), handler = { request ->
                assertEquals("/auth/v1/user", request.url.encodedPath)
                assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                respond("""{"id":"restored-user","email":"ignored"}""")
            }) { source ->
                assertEquals("restored-user", source.fetchSession()?.userId)
            }
        }

    @Test
    fun `401 refreshes once persists the rotated pair and retries with the new access token`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            var requests = 0
            withSource(storage, handler = { request ->
                requests++
                when (requests) {
                    1 -> respond("", HttpStatusCode.Unauthorized)
                    2 -> {
                        assertEquals("refresh_token", request.url.parameters["grant_type"])
                        assertNull(request.headers[HttpHeaders.Authorization])
                        assertTrue((request.body as TextContent).text.contains("refresh-secret"))
                        respond(rotatedTokenResponse())
                    }
                    else -> {
                        assertEquals(
                            "Bearer rotated-access",
                            request.headers[HttpHeaders.Authorization],
                        )
                        respond("""{"id":"user"}""")
                    }
                }
            }) { source ->
                assertEquals("user", source.fetchSession()?.userId)
                assertEquals(
                    tokens(accessToken = "rotated-access", refreshToken = "rotated-refresh"),
                    storage.tokens,
                )
                assertEquals(3, requests)
            }
        }

    @Test
    fun `invalid refresh clears the persisted session`() =
        runTest {
            val statuses =
                listOf(
                    HttpStatusCode.BadRequest,
                    HttpStatusCode.Unauthorized,
                    HttpStatusCode.Forbidden,
                )
            for (status in statuses) {
                val storage = FakeAuthTokenLocalDataSource(tokens())
                withSource(storage, handler = { request ->
                    if (request.url.encodedPath.endsWith("user")) {
                        respond("", HttpStatusCode.Unauthorized)
                    } else {
                        respond("refresh-secret", status)
                    }
                }) { source ->
                    assertNull(source.fetchSession())
                    assertEquals(SessionTokenRecord(), storage.tokens)
                }
            }
        }

    @Test
    fun `a second 401 clears the session without refreshing again`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            var requests = 0
            withSource(storage, handler = { request ->
                requests++
                if (request.url.parameters["grant_type"] == "refresh_token") {
                    respond(rotatedTokenResponse())
                } else {
                    respond("", HttpStatusCode.Unauthorized)
                }
            }) { source ->
                assertNull(source.fetchSession())
                assertEquals(SessionTokenRecord(), storage.tokens)
                assertEquals(3, requests)
            }
        }

    @Test
    fun `transient refresh failure preserves the stored pair and hides the response`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            withSource(storage, handler = { request ->
                if (request.url.encodedPath.endsWith("user")) {
                    respond("", HttpStatusCode.Unauthorized)
                } else {
                    respond("refresh-secret", HttpStatusCode.ServiceUnavailable)
                }
            }) { source ->
                assertRedacted(runCatching { source.fetchSession() }.exceptionOrNull())
                assertEquals(tokens(), storage.tokens)
            }
        }

    @Test
    fun `malformed token response does not persist credentials or leak parsing details`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource()
            withSource(storage, handler = {
                respond("""{"access_token":"access-secret","refresh_token":{}}""")
            }) { source ->
                val failure =
                    runCatching { source.signInWithGoogle(credential()) }.exceptionOrNull()
                assertRedacted(failure)
                assertEquals(SessionTokenRecord(), storage.tokens)
            }
        }

    @Test
    fun `transport failure hides credential and token contents`() =
        runTest {
            withSource(handler = {
                error("google-secret raw-nonce-secret access-secret refresh-secret")
            }) { source ->
                val failure =
                    runCatching { source.signInWithGoogle(credential()) }.exceptionOrNull()
                assertRedacted(failure)
            }
        }

    @Test
    fun `logout clears local tokens before the remote logout fails`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            withSource(storage, handler = { request ->
                assertEquals(SessionTokenRecord(), storage.tokens)
                assertEquals(HttpMethod.Post, request.method)
                assertEquals("local", request.url.parameters["scope"])
                assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                respond("access-secret", HttpStatusCode.InternalServerError)
            }) { source ->
                val failure = runCatching { AuthRepositoryImpl(source).signOut() }.exceptionOrNull()
                assertRedacted(failure)
                assertEquals(SessionTokenRecord(), storage.tokens)
            }
        }

    @Test
    fun `request cancellation propagates without clearing an existing session`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            withSource(storage, handler = { throw CancellationException("cancelled") }) { source ->
                val failure = runCatching { source.fetchSession() }.exceptionOrNull()
                assertTrue(failure is CancellationException)
                assertEquals(tokens(), storage.tokens)
            }
        }

    @Test
    fun `concurrent restores and logout share rotated tokens and leave no session`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            val refreshStarted = CompletableDeferred<Unit>()
            val releaseRefresh = CompletableDeferred<Unit>()
            var refreshRequests = 0
            withSource(storage, handler = { request ->
                when {
                    request.url.parameters["grant_type"] == "refresh_token" -> {
                        refreshRequests++
                        refreshStarted.complete(Unit)
                        releaseRefresh.await()
                        respond(rotatedTokenResponse())
                    }
                    request.headers[HttpHeaders.Authorization] == "Bearer access-secret" ->
                        respond("", HttpStatusCode.Unauthorized)
                    else -> {
                        assertEquals(
                            "Bearer rotated-access",
                            request.headers[HttpHeaders.Authorization],
                        )
                        respond("""{"id":"user"}""")
                    }
                }
            }) { source ->
                val restore = async { source.fetchSession() }
                refreshStarted.await()
                val anotherRestore = async { source.fetchSession() }
                val logout = async { source.signOut() }
                releaseRefresh.complete(Unit)
                assertEquals("user", restore.await()?.userId)
                assertEquals("user", anotherRestore.await()?.userId)
                logout.await()
                assertEquals(SessionTokenRecord(), storage.tokens)
                assertEquals(1, refreshRequests)
            }
        }

    @Test
    fun `cancelling a refresh caller still persists rotation before another restore`() =
        runTest {
            val storage = FakeAuthTokenLocalDataSource(tokens())
            val refreshStarted = CompletableDeferred<Unit>()
            val releaseRefresh = CompletableDeferred<Unit>()
            var refreshRequests = 0
            withSource(storage, handler = { request ->
                when {
                    request.url.parameters["grant_type"] == "refresh_token" -> {
                        refreshRequests++
                        refreshStarted.complete(Unit)
                        releaseRefresh.await()
                        respond(rotatedTokenResponse())
                    }
                    request.headers[HttpHeaders.Authorization] == "Bearer access-secret" ->
                        respond("", HttpStatusCode.Unauthorized)
                    else -> respond("""{"id":"user"}""")
                }
            }) { source ->
                val restore = launch { source.fetchSession() }
                refreshStarted.await()
                restore.cancel()
                releaseRefresh.complete(Unit)
                restore.join()
                assertTrue(restore.isCancelled)
                assertEquals(
                    tokens(accessToken = "rotated-access", refreshToken = "rotated-refresh"),
                    storage.tokens,
                )
                assertEquals("user", source.fetchSession()?.userId)
                assertEquals(1, refreshRequests)
            }
        }

    private suspend fun withSource(
        storage: FakeAuthTokenLocalDataSource = FakeAuthTokenLocalDataSource(),
        publishableKey: String = "sb_publishable_test",
        handler: MockRequestHandler,
        block: suspend (SupabaseAuthDataSource) -> Unit,
    ) {
        HttpClient(MockEngine(handler)).use { client ->
            block(
                SupabaseAuthDataSource(
                    httpClient = client,
                    projectUrl = "https://project.supabase.co",
                    publishableKey = publishableKey,
                    tokenLocalDataSource = storage,
                ),
            )
        }
    }

    private fun credential(): GoogleLoginCredential =
        GoogleLoginCredential(idToken = "google-secret", rawNonce = "raw-nonce-secret")

    private fun tokens(
        accessToken: String = "access-secret",
        refreshToken: String = "refresh-secret",
    ) = SessionTokenRecord(accessToken = accessToken, refreshToken = refreshToken)

    private fun rotatedTokenResponse(): String =
        tokenResponse(accessToken = "rotated-access", refreshToken = "rotated-refresh")

    private fun tokenResponse(
        accessToken: String = "access-secret",
        refreshToken: String = "refresh-secret",
    ): String =
        """
        {"access_token":"$accessToken","refresh_token":"$refreshToken","user":{"id":"user"}}
        """.trimIndent()

    private fun assertRedacted(failure: Throwable?) {
        assertTrue(failure is AuthDataSourceException)
        val secrets = listOf("google-secret", "raw-nonce-secret", "access-secret", "refresh-secret")
        for (exception in generateSequence(failure) { it.cause }) {
            assertTrue(secrets.none { exception.toString().contains(it) })
        }
    }
}
