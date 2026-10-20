package com.yapp.plus.data.supabase

import com.yapp.plus.core.datastore.SessionTokenRecord
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseRestClientTest {
    @Test
    fun `REST 401 restores and refreshes Auth then retries with persisted rotated token`() =
        runTest {
            val paths = mutableListOf<String>()
            SupabaseTestFixture(handler = { request ->
                paths += request.url.encodedPath
                assertEquals("project.supabase.co", request.url.host)
                assertEquals("sb_publishable_test", request.headers["apikey"])
                val authorization = request.headers[HttpHeaders.Authorization]
                when {
                    request.url.encodedPath == "/auth/v1/token" -> {
                        assertNull(authorization)
                        respond(
                            """{"access_token":"rotated","refresh_token":"renewed",
                            "user":{"id":"u"}}""",
                        )
                    }
                    authorization == "Bearer access-secret" ->
                        respond("access-secret", HttpStatusCode.Unauthorized)
                    request.url.encodedPath == "/auth/v1/user" -> respond("""{"id":"u"}""")
                    else -> {
                        assertEquals("Bearer rotated", authorization)
                        assertEquals("eq.SESSION", request.url.parameters["notice_type"])
                        respond("[]")
                    }
                }
            }).use { fixture ->
                val parameters = listOf("notice_type" to "eq.SESSION")
                assertEquals("[]", fixture.client.fetch("mvp_notices", parameters) { it })
                assertEquals("rotated", fixture.storage.tokens.accessToken)
                assertEquals(
                    listOf(
                        "/rest/v1/mvp_notices",
                        "/auth/v1/user",
                        "/auth/v1/token",
                        "/auth/v1/user",
                        "/rest/v1/mvp_notices",
                    ),
                    paths,
                )
            }
        }

    @Test
    fun `second REST 401 fails without another restore or retry`() =
        runTest {
            var restRequests = 0
            var authRequests = 0
            SupabaseTestFixture(handler = { request ->
                if (request.url.encodedPath.startsWith("/rest/")) {
                    restRequests++
                    respond("access-secret", HttpStatusCode.Unauthorized)
                } else {
                    authRequests++
                    respond("""{"id":"u"}""")
                }
            }).use { fixture ->
                assertFetchFailure(fixture)
                assertEquals(2, restRequests)
                assertEquals(1, authRequests)
            }
        }

    @Test
    fun `missing or invalid session makes no unauthenticated REST retry`() =
        runTest {
            val noSession = SessionTokenRecord()
            var requests = 0
            SupabaseTestFixture(handler = {
                requests++
                error("Unexpected request")
            }, tokens = noSession).use {
                assertFetchFailure(it)
                assertEquals(0, requests)
            }
            SupabaseTestFixture(handler = { request ->
                requests++
                val status =
                    if (request.url.encodedPath.endsWith("token")) {
                        HttpStatusCode.BadRequest
                    } else {
                        HttpStatusCode.Unauthorized
                    }
                respond("refresh-secret", status)
            }).use {
                assertFetchFailure(it)
                assertEquals(SessionTokenRecord(), it.storage.tokens)
                assertEquals(3, requests)
            }
        }

    @Test
    fun `malformed payload and transport failure are sanitized while cancellation propagates`() =
        runTest {
            SupabaseTestFixture(handler = { respond("access-secret") }).use { fixture ->
                val failure =
                    runCatching {
                        fixture.client.fetch("mvp_notices") { Json.decodeFromString<List<Int>>(it) }
                    }.exceptionOrNull()
                assertSanitized(failure)
            }
            SupabaseTestFixture(handler = { error("refresh-secret") }).use {
                assertFetchFailure(it)
            }
            val fixture = SupabaseTestFixture(handler = { throw CancellationException("cancel") })
            fixture.use {
                val result = runCatching { it.client.fetch("mvp_notices") { it } }
                val failure = result.exceptionOrNull()
                assertTrue(failure is CancellationException)
            }
        }

    private fun assertSanitized(failure: Throwable?) {
        assertTrue(failure is SupabaseDataSourceException)
        assertNull(failure?.cause)
        assertTrue(failure?.message?.contains("secret") == false)
    }

    private suspend fun assertFetchFailure(fixture: SupabaseTestFixture) {
        val failure = runCatching { fixture.client.fetch("mvp_notices") { it } }.exceptionOrNull()
        assertSanitized(failure)
    }
}
