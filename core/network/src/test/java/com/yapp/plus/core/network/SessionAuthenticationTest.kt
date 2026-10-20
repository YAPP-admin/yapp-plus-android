package com.yapp.plus.core.network

import com.yapp.plus.core.network.auth.RefreshResult
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.core.network.auth.Tokens
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class SessionAuthenticationTest {
    private val expired = Tokens("old-access", "old-refresh")
    private val store = TokenStore(expired)

    @Test
    fun `renewal retries original method body query and headers with rotated tokens`() =
        runTest {
            val calls = AtomicInteger()
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("value", request.url.parameters["query"])
                    assertEquals("request-key", request.headers["Idempotency-Key"])
                    assertEquals("payload", (request.body as TextContent).text)
                    val authorization = request.headers[HttpHeaders.Authorization]
                    val status =
                        if (calls.incrementAndGet() == 1) {
                            assertEquals("Bearer old-access", authorization)
                            HttpStatusCode.Unauthorized
                        } else {
                            assertEquals("Bearer new-access", authorization)
                            HttpStatusCode.OK
                        }
                    respond("", status)
                }
            createClient(engine) { refreshToken ->
                assertEquals("old-refresh", refreshToken)
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                val response =
                    client.post("resource?query=value") {
                        header("Idempotency-Key", "request-key")
                        setBody("payload")
                    }
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(2, calls.get())
                assertEquals("new-refresh", store.currentTokens()?.refreshToken)
            }
        }

    @Test
    fun `twenty concurrent unauthorized requests share one renewal`() =
        runTest {
            val sent = AtomicInteger()
            val allSent = CompletableDeferred<Unit>()
            val release = CompletableDeferred<Unit>()
            val renewals = AtomicInteger()
            val engine =
                MockEngine { request ->
                    if (request.headers[HttpHeaders.Authorization] == "Bearer old-access") {
                        if (sent.incrementAndGet() == 20) allSent.complete(Unit)
                        release.await()
                        respond("", HttpStatusCode.Unauthorized)
                    } else {
                        assertEquals(
                            "Bearer new-access",
                            request.headers[HttpHeaders.Authorization],
                        )
                        respond("", HttpStatusCode.OK)
                    }
                }
            createClient(engine) {
                renewals.incrementAndGet()
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                val requests = List(20) { async { client.get("resource/$it").status } }
                allSent.await()
                release.complete(Unit)
                assertTrue(requests.awaitAll().all { it == HttpStatusCode.OK })
                assertEquals(1, renewals.get())
            }
        }

    @Test
    fun `late unauthorized response reuses completed renewal`() =
        runTest {
            val slowSent = CompletableDeferred<Unit>()
            val releaseSlow = CompletableDeferred<Unit>()
            val renewals = AtomicInteger()
            val engine =
                MockEngine { request ->
                    if (request.headers[HttpHeaders.Authorization] == "Bearer old-access") {
                        if (request.url.encodedPath.endsWith("slow")) {
                            slowSent.complete(Unit)
                            releaseSlow.await()
                        }
                        respond("", HttpStatusCode.Unauthorized)
                    } else {
                        respond("", HttpStatusCode.OK)
                    }
                }
            createClient(engine) {
                renewals.incrementAndGet()
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                val slow = async { client.get("slow").status }
                slowSent.await()
                assertEquals(HttpStatusCode.OK, client.get("fast").status)
                releaseSlow.complete(Unit)
                assertEquals(HttpStatusCode.OK, slow.await())
                assertEquals(1, renewals.get())
            }
        }

    @Test
    fun `retry unauthorized response does not renew again`() =
        runTest {
            val calls = AtomicInteger()
            val renewals = AtomicInteger()
            val engine =
                MockEngine {
                    calls.incrementAndGet()
                    respond("", HttpStatusCode.Unauthorized)
                }
            createClient(engine) {
                renewals.incrementAndGet()
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                assertTrue(
                    runCatching { client.get("resource") }.exceptionOrNull()
                        is ClientRequestException,
                )
                assertEquals(2, calls.get())
                assertEquals(1, renewals.get())
            }
        }

    @Test
    fun `missing refresh token and external origin never renew`() =
        runTest {
            store.updateTokens(Tokens("access-only", null))
            val renewals = AtomicInteger()
            val engine =
                MockEngine { request ->
                    if (request.url.host == "external.test") {
                        assertNull(request.headers[HttpHeaders.Authorization])
                    }
                    respond("", HttpStatusCode.Unauthorized)
                }
            createClient(engine) {
                renewals.incrementAndGet()
                RefreshResult.Rejected
            }.use { client ->
                assertTrue(
                    runCatching { client.get("resource") }.exceptionOrNull()
                        is ClientRequestException,
                )
                store.updateTokens(expired)
                assertTrue(
                    runCatching { client.get("https://external.test/resource") }.exceptionOrNull()
                        is ClientRequestException,
                )
                assertEquals(0, renewals.get())
            }
        }

    @Test
    fun `rejected refresh invalidates the session without retry`() =
        runTest {
            val calls = AtomicInteger()
            val engine =
                MockEngine {
                    calls.incrementAndGet()
                    respond("", HttpStatusCode.Unauthorized)
                }
            createClient(engine) { RefreshResult.Rejected }.use { client ->
                runCatching { client.get("resource") }
                assertNull(store.currentTokens())
                assertEquals(1, calls.get())
            }
        }

    @Test
    fun `concurrent requests share failed renewal but later request can recover`() =
        runTest {
            val slowSent = CompletableDeferred<Unit>()
            val releaseSlow = CompletableDeferred<Unit>()
            val renewals = AtomicInteger()
            val failure = IOException("offline")
            val engine =
                MockEngine { request ->
                    if (request.headers[HttpHeaders.Authorization] == "Bearer old-access") {
                        if (request.url.encodedPath.endsWith("slow")) {
                            slowSent.complete(Unit)
                            releaseSlow.await()
                        }
                        respond("", HttpStatusCode.Unauthorized)
                    } else {
                        respond("", HttpStatusCode.OK)
                    }
                }
            createClient(engine) {
                if (renewals.incrementAndGet() == 1) throw failure
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                val slow = async { runCatching { client.get("slow") }.exceptionOrNull() }
                slowSent.await()
                val fastFailure = runCatching { client.get("fast") }.exceptionOrNull()
                assertTrue(fastFailure is IOException)
                assertEquals("offline", fastFailure?.message)
                releaseSlow.complete(Unit)
                val slowFailure = slow.await()
                assertTrue(slowFailure is IOException)
                assertEquals("offline", slowFailure?.message)
                assertSame(expired, store.currentTokens())
                assertEquals(1, renewals.get())
                assertEquals(HttpStatusCode.OK, client.get("recover").status)
                assertEquals(2, renewals.get())
            }
        }

    @Test
    fun `next expiry renews using the rotated refresh token`() =
        runTest {
            val calls = AtomicInteger()
            val renewals = AtomicInteger()
            val engine =
                MockEngine {
                    val status =
                        if (calls.incrementAndGet() % 2 == 1) {
                            HttpStatusCode.Unauthorized
                        } else {
                            HttpStatusCode.OK
                        }
                    respond("", status)
                }
            createClient(engine) { token ->
                val version = renewals.incrementAndGet()
                assertEquals(if (version == 1) "old-refresh" else "refresh-1", token)
                RefreshResult.Success(Tokens("access-$version", "refresh-$version"))
            }.use { client ->
                assertEquals(HttpStatusCode.OK, client.get("first").status)
                assertEquals(HttpStatusCode.OK, client.get("second").status)
                assertEquals(2, renewals.get())
                assertEquals("refresh-2", store.currentTokens()?.refreshToken)
            }
        }

    @Test
    fun `cancelling the initiating request keeps renewal alive for other requests`() =
        runTest {
            val started = CompletableDeferred<Unit>()
            val release = CompletableDeferred<Unit>()
            val renewals = AtomicInteger()
            val engine =
                MockEngine { request ->
                    respond(
                        "",
                        if (request.headers[HttpHeaders.Authorization] == "Bearer old-access") {
                            HttpStatusCode.Unauthorized
                        } else {
                            HttpStatusCode.OK
                        },
                    )
                }
            createClient(engine) {
                renewals.incrementAndGet()
                started.complete(Unit)
                release.await()
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                val first = async { client.get("first") }
                started.await()
                val second = async { client.get("second").status }
                first.cancel()
                release.complete(Unit)
                assertEquals(HttpStatusCode.OK, second.await())
                assertEquals(1, renewals.get())
            }
        }

    @Test
    fun `renewal cannot restore a cleared or replaced session`() =
        runTest {
            val started = CompletableDeferred<Unit>()
            val release = CompletableDeferred<Unit>()
            val replacement = Tokens("other-access", "other-refresh")
            val engine = MockEngine { respond("", HttpStatusCode.Unauthorized) }
            createClient(engine) {
                started.complete(Unit)
                release.await()
                RefreshResult.Success(Tokens("new-access", "new-refresh"))
            }.use { client ->
                val request = async { runCatching { client.get("resource") }.exceptionOrNull() }
                started.await()
                store.updateTokens(null)
                store.updateTokens(replacement)
                release.complete(Unit)
                assertTrue(request.await() is ClientRequestException)
                assertSame(replacement, store.currentTokens())
            }
        }

    private fun TestScope.createClient(
        engine: MockEngine,
        refresher: TokenRefresher,
    ): HttpClient =
        ApiHttpClientFactory.create(
            engine = engine,
            baseUrl = "https://api.example.test/api/",
            tokenStore = store,
            tokenRefresher = refresher,
            renewalScope = backgroundScope,
        )
}
