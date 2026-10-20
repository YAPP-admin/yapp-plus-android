package com.yapp.plus.core.network.auth

import com.yapp.plus.core.network.ApiHttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class RestoredSessionAuthenticationTest {
    private val saved = Tokens(accessToken = "saved-access", refreshToken = "saved-refresh")
    private val rotated = Tokens(accessToken = "new-access", refreshToken = "new-refresh")

    @Test
    fun `twenty restored requests renew once and persist before retry`() =
        runTest {
            val persistence = FakeTokenPersistence(saved)
            val store = TokenStore(persistence = persistence)
            val allSent = CompletableDeferred<Unit>()
            val release = CompletableDeferred<Unit>()
            val expiredRequests = AtomicInteger()
            val retriedRequests = AtomicInteger()
            val renewals = AtomicInteger()
            val engine =
                MockEngine { request ->
                    if (request.headers[HttpHeaders.Authorization] == "Bearer saved-access") {
                        val sentCount = expiredRequests.incrementAndGet()
                        if (sentCount == REQUEST_COUNT) {
                            allSent.complete(Unit)
                        }
                        release.await()
                        respond("", HttpStatusCode.Unauthorized)
                    } else {
                        assertEquals(
                            "Bearer new-access",
                            request.headers[HttpHeaders.Authorization],
                        )
                        assertEquals(rotated.refreshToken, persistence.storedTokens?.refreshToken)
                        retriedRequests.incrementAndGet()
                        respond("", HttpStatusCode.OK)
                    }
                }
            createClient(engine, store) { refreshToken ->
                assertEquals(saved.refreshToken, refreshToken)
                renewals.incrementAndGet()
                RefreshResult.Success(rotated)
            }.use { client ->
                val requests = List(REQUEST_COUNT) { async { client.get("sessions/$it").status } }
                allSent.await()
                release.complete(Unit)
                assertTrue(requests.awaitAll().all { it == HttpStatusCode.OK })
            }
            assertEquals(REQUEST_COUNT, expiredRequests.get())
            assertEquals(REQUEST_COUNT, retriedRequests.get())
            assertEquals(1, renewals.get())
            assertEquals(1, persistence.readCount)
            assertEquals(1, persistence.writeCount)
            assertEquals(
                rotated.refreshToken,
                TokenStore(persistence = persistence).currentTokens()?.refreshToken,
            )
        }

    @Test
    fun `client after restart renews with persisted rotated refresh token`() =
        runTest {
            val persistence = FakeTokenPersistence(saved)
            TokenStore(persistence = persistence).updateTokens(rotated)
            val restoredStore = TokenStore(persistence = persistence)
            val calls = AtomicInteger()
            val renewals = AtomicInteger()
            val next = Tokens(accessToken = "next-access", refreshToken = "next-refresh")
            val engine =
                MockEngine { request ->
                    val first = calls.incrementAndGet() == 1
                    assertEquals(
                        if (first) "Bearer new-access" else "Bearer next-access",
                        request.headers[HttpHeaders.Authorization],
                    )
                    respond("", if (first) HttpStatusCode.Unauthorized else HttpStatusCode.OK)
                }
            createClient(engine, restoredStore) { refreshToken ->
                assertEquals(rotated.refreshToken, refreshToken)
                renewals.incrementAndGet()
                RefreshResult.Success(next)
            }.use { client ->
                assertEquals(HttpStatusCode.OK, client.get("sessions/current-week").status)
            }
            assertEquals(2, calls.get())
            assertEquals(1, renewals.get())
            val restoredAgain = TokenStore(persistence = persistence).currentTokens()
            assertEquals(next.accessToken, restoredAgain?.accessToken)
            assertEquals(next.refreshToken, restoredAgain?.refreshToken)
        }

    private fun TestScope.createClient(
        engine: MockEngine,
        store: TokenStore,
        refresher: TokenRefresher,
    ): HttpClient =
        ApiHttpClientFactory.create(
            engine = engine,
            baseUrl = "https://api.example.test/api/",
            tokenStore = store,
            tokenRefresher = refresher,
            renewalScope = backgroundScope,
        )

    private companion object {
        const val REQUEST_COUNT = 20
    }
}
