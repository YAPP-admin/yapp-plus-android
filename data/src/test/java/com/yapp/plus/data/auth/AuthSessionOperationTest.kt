package com.yapp.plus.data.auth

import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.core.network.auth.Tokens
import com.yapp.plus.data.auth.datasource.auth.AuthRemoteDataSource
import com.yapp.plus.data.auth.repository.AuthRepositoryImpl
import com.yapp.plus.domain.auth.AuthOperationResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AuthSessionOperationTest {
    @Test
    fun logoutSendsRefreshTokenAndClearsStoredSessionAfterNoContent() =
        runTest {
            val persistence = FakeAuthTokenPersistence(initialTokens)
            val tokenStore = TokenStore(persistence = persistence)
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Delete, request.method)
                    assertEquals("/api/auth/sessions/current", request.url.encodedPath)
                    assertNull(request.headers[HttpHeaders.Authorization])
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals(setOf("refreshToken"), body.keys)
                    assertEquals("test-refresh", body.getValue("refreshToken").jsonPrimitive.content)
                    assertSame(initialTokens, tokenStore.currentTokens())
                    respond("", HttpStatusCode.NoContent)
                }
            createClient(engine).use { client ->
                val repository = AuthRepositoryImpl(AuthRemoteDataSource(client), tokenStore)
                assertEquals(AuthOperationResult.Success, repository.logout())
                assertNull(tokenStore.currentTokens())
                assertNull(persistence.tokens)
                assertEquals(1, persistence.updateCount)
            }
        }

    @Test
    fun withdrawalUsesAuthenticatedClientWithoutBodyAndClearsStoredSession() =
        runTest {
            val persistence = FakeAuthTokenPersistence(initialTokens)
            val tokenStore = TokenStore(persistence = persistence)
            val unauthenticatedEngine = MockEngine { error("Unexpected unauthenticated request") }
            val authenticatedEngine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Delete, request.method)
                    assertEquals("/api/users/me", request.url.encodedPath)
                    assertEquals("Bearer test-access", request.headers[HttpHeaders.Authorization])
                    assertTrue(request.body is OutgoingContent.NoContent)
                    assertSame(initialTokens, tokenStore.currentTokens())
                    respond("", HttpStatusCode.NoContent)
                }
            createClient(unauthenticatedEngine).use { unauthenticatedClient ->
                createClient(authenticatedEngine, "test-access").use { authenticatedClient ->
                    val source =
                        AuthRemoteDataSource(
                            unauthenticatedClient = unauthenticatedClient,
                            authenticatedClient = authenticatedClient,
                        )
                    val repository = AuthRepositoryImpl(source, tokenStore)
                    assertEquals(AuthOperationResult.Success, repository.withdraw())
                    assertNull(tokenStore.currentTokens())
                    assertNull(persistence.tokens)
                    assertEquals(1, persistence.updateCount)
                }
            }
        }

    @Test
    fun everyResponseOtherThanNoContentPreservesSessionForBothOperations() =
        runTest {
            val statuses =
                listOf(
                    HttpStatusCode.OK,
                    HttpStatusCode.BadRequest,
                    HttpStatusCode.Unauthorized,
                    HttpStatusCode.InternalServerError,
                )
            for (status in statuses) {
                val persistence = FakeAuthTokenPersistence(initialTokens)
                val tokenStore = TokenStore(persistence = persistence)
                createClient(MockEngine { respond("", status) }).use { client ->
                    val repository = AuthRepositoryImpl(AuthRemoteDataSource(client), tokenStore)
                    assertEquals(AuthOperationResult.Failure, repository.logout())
                    assertEquals(AuthOperationResult.Failure, repository.withdraw())
                    assertSame(initialTokens, tokenStore.currentTokens())
                    assertSame(initialTokens, persistence.tokens)
                    assertEquals(0, persistence.updateCount)
                }
            }
        }

    @Test
    fun remoteFailurePreservesSessionAndAllowsRetry() =
        runTest {
            val source = FakeAuthDataSource().apply { operation = { throw IOException("offline") } }
            val persistence = FakeAuthTokenPersistence(initialTokens)
            val tokenStore = TokenStore(persistence = persistence)
            val repository = AuthRepositoryImpl(source, tokenStore)

            assertEquals(AuthOperationResult.Failure, repository.logout())
            assertEquals(AuthOperationResult.Failure, repository.withdraw())
            assertSame(initialTokens, tokenStore.currentTokens())
            assertEquals(0, persistence.updateCount)

            source.operation = {}
            assertEquals(AuthOperationResult.Success, repository.logout())
            assertNull(tokenStore.currentTokens())
            assertNull(persistence.tokens)
        }

    @Test
    fun noStoredSessionRejectsBothOperationsWithoutCallingServer() =
        runTest {
            val source = FakeAuthDataSource()
            val repository = AuthRepositoryImpl(source, TokenStore())
            assertEquals(AuthOperationResult.Failure, repository.logout())
            assertEquals(AuthOperationResult.Failure, repository.withdraw())
            assertEquals(0, source.logoutRequestCount)
            assertEquals(0, source.withdrawalRequestCount)
        }

    @Test
    fun logoutWithoutRefreshTokenDoesNotCallServer() =
        runTest {
            val source = FakeAuthDataSource()
            val repository = AuthRepositoryImpl(source, TokenStore(Tokens("test-access", null)))
            assertEquals(AuthOperationResult.Failure, repository.logout())
            assertEquals(0, source.logoutRequestCount)
        }

    @Test
    fun cancellationPropagatesAndPreservesSession() =
        runTest {
            val source =
                FakeAuthDataSource().apply {
                    operation = { throw CancellationException("cancelled") }
                }
            val persistence = FakeAuthTokenPersistence(initialTokens)
            val tokenStore = TokenStore(persistence = persistence)
            val repository = AuthRepositoryImpl(source, tokenStore)

            assertTrue(runCatching { repository.logout() }.exceptionOrNull() is CancellationException)
            assertTrue(runCatching { repository.withdraw() }.exceptionOrNull() is CancellationException)
            assertSame(initialTokens, tokenStore.currentTokens())
            assertEquals(0, persistence.updateCount)
        }

    @Test
    fun localDeletionFailureIsNotReportedAsSuccess() =
        runTest {
            val persistence =
                FakeAuthTokenPersistence(initialTokens).apply {
                    updateFailure = IOException("storage unavailable")
                }
            val tokenStore = TokenStore(persistence = persistence)
            val repository = AuthRepositoryImpl(FakeAuthDataSource(), tokenStore)
            assertEquals(AuthOperationResult.Failure, repository.logout())
            assertEquals(AuthOperationResult.Failure, repository.withdraw())
            assertSame(initialTokens, tokenStore.currentTokens())
            assertSame(initialTokens, persistence.tokens)
            assertEquals(2, persistence.updateCount)
        }

    private fun createClient(
        engine: MockEngine,
        accessToken: String? = null,
    ): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(DefaultRequest) {
                url("https://api.example.test/api/")
                if (accessToken != null) headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    private val initialTokens = Tokens("test-access", "test-refresh")
}
