package com.yapp.plus.data.attendance

import com.yapp.plus.data.apiTestClient
import com.yapp.plus.domain.attendance.AttendanceException
import com.yapp.plus.domain.attendance.AttendanceReportType
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AttendanceReportApiTest {
    @Test fun lateUsesCurrentServerEnumAndConflictKeepsMachineReadableCode() =
        runTest {
            var requests = 0
            apiTestClient(
                MockEngine { request ->
                    assertEquals("/api/attendance-reports", request.url.encodedPath)
                    val body =
                        Json
                            .parseToJsonElement(
                                (request.body as TextContent).text,
                            ).jsonObject
                    assertEquals("LATE", body.getValue("type").jsonPrimitive.content)
                    assertEquals("개인 일정", body.getValue("reason").jsonPrimitive.content)
                    if (requests++ == 0) {
                        respond(
                            """{"sessionId":42,"submittedAt":"2026-10-30T05:00:00Z"}""",
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    } else {
                        respond(
                            """{"code":"ATTENDANCE_REPORT_SESSION_CHANGED"}""",
                            HttpStatusCode.Conflict,
                            headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                },
            ).use { client ->
                val repository = AttendanceRepositoryImpl(AttendanceRemoteDataSource(client))
                assertEquals(42L, repository.submitReport(AttendanceReportType.LATE, "개인 일정"))
                val error =
                    runCatching {
                        repository.submitReport(AttendanceReportType.LATE, "개인 일정")
                    }.exceptionOrNull() as AttendanceException
                assertEquals("ATTENDANCE_REPORT_SESSION_CHANGED", error.code)
                assertFalse(error.isInvalidCode)
            }
        }
}
