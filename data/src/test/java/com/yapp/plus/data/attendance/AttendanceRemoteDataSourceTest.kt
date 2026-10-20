package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceRemoteDataSourceTest {
    @Test
    fun `verification sends numeric code and accepts confirmed timestamp`() =
        runTest {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/api/attendance-verifications", request.url.encodedPath)
                    assertEquals(
                        Json.parseToJsonElement("""{"sessionId":42,"code":123}"""),
                        Json.parseToJsonElement((request.body as TextContent).text),
                    )
                    respond(
                        """{"verified":true,"checkedInAt":"2026-10-24T05:00:00Z"}""",
                        headers = jsonHeaders,
                    )
                }
            createClient(engine).use {
                AttendanceRemoteDataSource(it).verifyAttendance(sessionId = 42, code = 123)
            }
        }

    @Test
    fun `report serializes each existing report type and returns confirmed session`() =
        runTest {
            for (type in AttendanceReportType.entries) {
                val engine =
                    MockEngine { request ->
                        assertEquals(HttpMethod.Post, request.method)
                        assertEquals("/api/attendance-reports", request.url.encodedPath)
                        assertEquals(
                            Json.parseToJsonElement("""{"type":"${type.name}","reason":"사전 보고"}"""),
                            Json.parseToJsonElement((request.body as TextContent).text),
                        )
                        respond(
                            """{"sessionId":42,"submittedAt":"2026-10-24T05:00:00Z"}""",
                            headers = jsonHeaders,
                        )
                    }
                createClient(engine).use {
                    val result =
                        AttendanceRemoteDataSource(it).submitReport(type = type, reason = "사전 보고")
                    assertEquals(42L, result)
                }
            }
        }

    @Test
    fun `non success responses keep invalid code classification for both operations`() =
        runTest {
            for (code in listOf("INVALID_ATTENDANCE_CODE", "OTHER_ERROR")) {
                val engine =
                    MockEngine {
                        respond("""{"code":"$code"}""", HttpStatusCode.BadRequest, jsonHeaders)
                    }
                createClient(engine).use {
                    val source = AttendanceRemoteDataSource(it)
                    val failures =
                        listOf(
                            runCatching {
                                source.verifyAttendance(sessionId = 42, code = 1234)
                            }.exceptionOrNull(),
                            runCatching {
                                source.submitReport(AttendanceReportType.ABSENT, "사유")
                            }.exceptionOrNull(),
                        )
                    for (failure in failures) {
                        assertTrue(failure is AttendanceException)
                        assertEquals(
                            code == "INVALID_ATTENDANCE_CODE",
                            (failure as AttendanceException).isInvalidCode,
                        )
                    }
                }
            }
        }

    @Test
    fun `unconfirmed results and invalid timestamps remain failures`() =
        runTest {
            val verifications =
                listOf(
                    """{"verified":false,"checkedInAt":"2026-10-24T05:00:00Z"}""",
                    """{"verified":true,"checkedInAt":"invalid"}""",
                )
            for (response in verifications) {
                createClient(MockEngine { respond(response, headers = jsonHeaders) }).use {
                    assertTrue(
                        runCatching {
                            AttendanceRemoteDataSource(it).verifyAttendance(42, 1234)
                        }.isFailure,
                    )
                }
            }
            val reports =
                listOf(
                    """{"sessionId":0,"submittedAt":"2026-10-24T05:00:00Z"}""",
                    """{"sessionId":42,"submittedAt":"invalid"}""",
                )
            for (response in reports) {
                createClient(MockEngine { respond(response, headers = jsonHeaders) }).use {
                    assertTrue(
                        runCatching {
                            val source = AttendanceRemoteDataSource(it)
                            source.submitReport(AttendanceReportType.ABSENT, "사유")
                        }.isFailure,
                    )
                }
            }
        }

    private fun createClient(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(DefaultRequest) { url("https://api.example.test/api/") }
            install(ContentNegotiation) { json() }
        }

    private val jsonHeaders =
        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
}
