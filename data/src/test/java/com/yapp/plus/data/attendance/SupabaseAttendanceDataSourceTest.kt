package com.yapp.plus.data.attendance

import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.data.supabase.SupabaseTestFixture
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseAttendanceDataSourceTest {
    @Test
    fun `RPC sends authenticated JSON with long ID and padded ASCII code`() =
        runTest {
            val sessionId = 4_294_967_296L
            val codes = listOf(0 to "0000", 1 to "0001", 123 to "0123", 9999 to "9999")
            for ((code, padded) in codes) {
                SupabaseTestFixture(handler = { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("project.supabase.co", request.url.host)
                    assertEquals(RPC_PATH, request.url.encodedPath)
                    assertTrue(request.url.parameters.isEmpty())
                    assertEquals("sb_publishable_test", request.headers["apikey"])
                    assertEquals("Bearer access-secret", request.headers[HttpHeaders.Authorization])
                    assertEquals(ContentType.Application.Json, request.body.contentType)
                    assertEquals(
                        Json.parseToJsonElement(
                            """{"p_session_id":$sessionId,"p_code":"$padded"}""",
                        ),
                        Json.parseToJsonElement((request.body as TextContent).text),
                    )
                    respond(confirmation(sessionId))
                }).use { fixture ->
                    SupabaseAttendanceDataSource(fixture.client).verifyAttendance(sessionId, code)
                }
            }
        }

    @Test
    fun `code outside four digit bounds and invalid session make no request`() =
        runTest {
            var requests = 0
            SupabaseTestFixture(handler = {
                requests++
                error("Unexpected request")
            }).use { fixture ->
                val source = SupabaseAttendanceDataSource(fixture.client)
                for (code in listOf(-1, 10000, Int.MIN_VALUE, Int.MAX_VALUE)) {
                    assertFailure(
                        runCatching { source.verifyAttendance(sessionId = 42, code = code) },
                        isInvalidCode = true,
                    )
                }
                assertFailure(runCatching { source.verifyAttendance(sessionId = 0, code = 1234) })
                assertEquals(0, requests)
            }
        }

    @Test
    fun `repeated verification accepts original committed timestamp without client clock checks`() =
        runTest {
            var requests = 0
            SupabaseTestFixture(handler = {
                requests++
                respond(confirmation(timestamp = "2020-01-01T09:00:00+09:00"))
            }).use { fixture ->
                val source = SupabaseAttendanceDataSource(fixture.client)
                repeat(2) { source.verifyAttendance(sessionId = 42, code = 1234) }
                assertEquals(2, requests)
            }
        }

    @Test
    fun `success requires exactly one matching verified row and valid timestamp`() =
        runTest {
            val row = confirmation().removeSurrounding("[", "]")
            val responses =
                listOf(
                    "[]",
                    "[$row,$row]",
                    row,
                    "{}",
                    "access-secret",
                    confirmation(sessionId = 43),
                    confirmation().replace("true", "false"),
                    confirmation(timestamp = "access-secret"),
                    confirmation().replace("\"$TIMESTAMP\"", "null"),
                    """[{"session_id":42,"verified":true}]""",
                    """[{"verified":true,"checked_in_at":"$TIMESTAMP"}]""",
                    """[{"session_id":42,"checked_in_at":"$TIMESTAMP"}]""",
                )
            for (response in responses) {
                SupabaseTestFixture(handler = { respond(response) }).use { fixture ->
                    assertVerificationFailure(fixture)
                }
            }
        }

    @Test
    fun `PostgREST message marker and legacy code marker preserve invalid code classification`() =
        runTest {
            val errors =
                listOf(
                    """{"code":"22023","message":"INVALID_ATTENDANCE_CODE",
                    "details":"access-secret","hint":"refresh-secret"}""",
                    """{"code":"INVALID_ATTENDANCE_CODE"}""",
                )
            for (error in errors) {
                SupabaseTestFixture(handler = {
                    respond(error, HttpStatusCode.BadRequest)
                }).use { fixture -> assertVerificationFailure(fixture, isInvalidCode = true) }
            }
        }

    @Test
    fun `authentication eligibility window and malformed errors are generic and sanitized`() =
        runTest {
            val errors =
                listOf(
                    """{"code":"42501","message":"AUTHENTICATION_REQUIRED"}""",
                    """{"code":"42501","message":"ATTENDANCE_NOT_ELIGIBLE"}""",
                    """{"code":"P0001","message":"ATTENDANCE_NOT_OPEN"}""",
                    """{"code":"22023","message":"access-secret"}""",
                    """{"details":"INVALID_ATTENDANCE_CODE","hint":"refresh-secret"}""",
                    """{"message":"access-secret INVALID_ATTENDANCE_CODE"}""",
                    """{"message":{"token":"access-secret"}}""",
                    "access-secret",
                    "[]",
                    "null",
                )
            for (error in errors) {
                SupabaseTestFixture(handler = {
                    respond(error, HttpStatusCode.BadRequest)
                }).use { fixture -> assertVerificationFailure(fixture) }
            }
            SupabaseTestFixture(handler = {
                respond("refresh-secret", HttpStatusCode.InternalServerError)
            }).use { fixture -> assertVerificationFailure(fixture) }
        }

    @Test
    fun `RPC 401 refresh retries once with identical body and rotated Supabase bearer token`() =
        runTest {
            val paths = mutableListOf<String>()
            val bodies = mutableListOf<String>()
            SupabaseTestFixture(handler = { request ->
                paths += request.url.encodedPath
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
                    authorization == "Bearer access-secret" -> {
                        if (request.url.encodedPath == RPC_PATH) {
                            bodies += (request.body as TextContent).text
                        }
                        respond("access-secret", HttpStatusCode.Unauthorized)
                    }
                    request.url.encodedPath == "/auth/v1/user" -> respond("""{"id":"u"}""")
                    else -> {
                        assertEquals(HttpMethod.Post, request.method)
                        assertEquals(RPC_PATH, request.url.encodedPath)
                        assertEquals("Bearer rotated", authorization)
                        assertEquals(ContentType.Application.Json, request.body.contentType)
                        bodies += (request.body as TextContent).text
                        respond(confirmation())
                    }
                }
            }).use { fixture ->
                SupabaseAttendanceDataSource(fixture.client).verifyAttendance(42, 123)
                assertEquals("rotated", fixture.storage.tokens.accessToken)
                assertEquals(2, bodies.size)
                assertEquals(bodies.first(), bodies.last())
                assertEquals(
                    listOf(
                        RPC_PATH,
                        "/auth/v1/user",
                        "/auth/v1/token",
                        "/auth/v1/user",
                        RPC_PATH,
                    ),
                    paths,
                )
            }
        }

    @Test
    fun `second RPC 401 is generic without another refresh or retry`() =
        runTest {
            var rpcRequests = 0
            var authRequests = 0
            SupabaseTestFixture(handler = { request ->
                if (request.url.encodedPath == RPC_PATH) {
                    rpcRequests++
                    respond("access-secret", HttpStatusCode.Unauthorized)
                } else {
                    authRequests++
                    respond("""{"id":"u"}""")
                }
            }).use { fixture ->
                assertVerificationFailure(fixture)
                assertEquals(2, rpcRequests)
                assertEquals(1, authRequests)
            }
        }

    @Test
    fun `missing session and rejected refresh never send an unauthenticated RPC`() =
        runTest {
            var requests = 0
            SupabaseTestFixture(handler = {
                requests++
                error("Unexpected request")
            }, tokens = SessionTokenRecord()).use { fixture ->
                assertVerificationFailure(fixture)
                assertEquals(0, requests)
            }
            SupabaseTestFixture(handler = { request ->
                requests++
                val status =
                    if (request.url.encodedPath == "/auth/v1/token") {
                        HttpStatusCode.BadRequest
                    } else {
                        HttpStatusCode.Unauthorized
                    }
                respond("refresh-secret", status)
            }).use { fixture ->
                assertVerificationFailure(fixture)
                assertEquals(3, requests)
                assertEquals(SessionTokenRecord(), fixture.storage.tokens)
            }
        }

    @Test
    fun `transport errors sanitize and RPC or refresh cancellation propagates`() =
        runTest {
            val failed = SupabaseTestFixture(handler = { error("access-secret refresh-secret") })
            failed.use { assertVerificationFailure(it) }
            for (cancelPath in listOf(RPC_PATH, "/auth/v1/token")) {
                SupabaseTestFixture(handler = { request ->
                    if (request.url.encodedPath == cancelPath) throw CancellationException("cancel")
                    respond("access-secret", HttpStatusCode.Unauthorized)
                }).use { fixture ->
                    val source = SupabaseAttendanceDataSource(fixture.client)
                    val result = runCatching { source.verifyAttendance(42, 1234) }
                    assertTrue(result.exceptionOrNull() is CancellationException)
                }
            }
        }

    @Test
    fun `unsupported reports fail generically without invoking a backend`() =
        runTest {
            var requests = 0
            SupabaseTestFixture(handler = {
                requests++
                error("Unexpected request")
            }).use { fixture ->
                val source = SupabaseAttendanceDataSource(fixture.client)
                for (type in AttendanceReportType.entries) {
                    assertFailure(runCatching { source.submitReport(type, "사전 보고") })
                }
                assertEquals(0, requests)
            }
        }

    private suspend fun assertVerificationFailure(
        fixture: SupabaseTestFixture,
        isInvalidCode: Boolean = false,
    ) {
        val source = SupabaseAttendanceDataSource(fixture.client)
        assertFailure(runCatching { source.verifyAttendance(42, 1234) }, isInvalidCode)
    }

    private fun assertFailure(
        result: Result<*>,
        isInvalidCode: Boolean = false,
    ) {
        val failure = result.exceptionOrNull()
        assertTrue(failure is AttendanceException)
        assertEquals(isInvalidCode, (failure as AttendanceException).isInvalidCode)
        assertEquals("Attendance request failed", failure.message)
        assertNull(failure.cause)
        assertEquals(0, failure.suppressed.size)
    }

    private fun confirmation(
        sessionId: Long = 42,
        timestamp: String = TIMESTAMP,
    ): String = """[{"session_id":$sessionId,"verified":true,"checked_in_at":"$timestamp"}]"""

    private companion object {
        const val RPC_PATH = "/rest/v1/rpc/mvp_verify_attendance"
        const val TIMESTAMP = "2026-10-24T05:00:00+00:00"
    }
}
