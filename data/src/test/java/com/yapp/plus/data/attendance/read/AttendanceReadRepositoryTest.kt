package com.yapp.plus.data.attendance.read

import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.AttendanceStaffDecision
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceReadRepositoryTest {
    @Test
    fun scoreUsesServerTotalsWithoutRecalculatingOverlappingCounts() =
        runTest {
            client(
                body = """{"generationId":12,"totalScore":86,"attendanceCount":3,
                "lateCount":1,"absentCount":0,"earlyLeaveCount":1}""",
            ).use {
                val score = repository(it).fetchScore()!!
                assertEquals(86, score.totalScore)
                assertEquals(3, score.attendanceCount)
                assertEquals(1, score.lateCount)
                assertEquals(1, score.earlyLeaveCount)
            }
        }

    @Test
    fun onlyDocumentedIneligibleAndNoGenerationErrorsHaveNoScore() =
        runTest {
            for ((status, code) in listOf(
                HttpStatusCode.Forbidden to "ATTENDANCE_SCORE_NOT_ELIGIBLE",
                HttpStatusCode.NotFound to "CURRENT_GENERATION_NOT_FOUND",
            )) {
                client(
                    """{"code":"$code"}""",
                    status,
                ).use { assertNull(repository(it).fetchScore()) }
            }
            for (status in listOf(
                HttpStatusCode.Unauthorized,
                HttpStatusCode.Conflict,
                HttpStatusCode.NotFound,
            )) {
                client("""{"code":"AUTHENTICATION_FAILED"}""", status).use {
                    assertTrue(runCatching { repository(it).fetchScore() }.isFailure)
                }
            }
        }

    @Test
    fun historyUsesOpaqueCursorAndPreservesEveryStatus() =
        runTest {
            val items =
                AttendanceResultStatus.entries
                    .mapIndexed { index, status ->
                        """{"sessionId":${index + 1},"sessionName":"세션",
                "attendanceStatus":"${status.name}","staffDecisionStatus":null}"""
                    }.joinToString(",")
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("/api/attendance-history", request.url.encodedPath)
                    assertEquals("opaque_-cursor", request.url.parameters["cursor"])
                    assertEquals(setOf("cursor"), request.url.parameters.names())
                    respond(
                        """{"summary":{"totalSessionCount":12,"startedSessionCount":7},
                    "items":[$items],"nextCursor":null}""",
                        headers =
                            headersOf(
                                HttpHeaders.ContentType,
                                ContentType.Application.Json.toString(),
                            ),
                    )
                }
            configuredClient(engine).use {
                val page = repository(it).fetchHistory("opaque_-cursor")
                assertEquals(12, page.totalSessionCount)
                assertEquals(7, page.startedSessionCount)
                assertEquals(AttendanceResultStatus.entries, page.items.map { item -> item.status })
                assertNull(page.nextCursor)
            }
        }

    @Test
    fun initialHistoryOmitsCursorAndEmptyResponseIsSuccessful() =
        runTest {
            val engine =
                MockEngine { request ->
                    assertTrue(request.url.parameters.isEmpty())
                    respond(
                        """{"summary":{"totalSessionCount":0,"startedSessionCount":0},
                    "items":[],"nextCursor":null}""",
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            configuredClient(engine).use {
                val page = repository(it).fetchHistory()
                assertTrue(page.items.isEmpty())
                assertEquals(0, page.totalSessionCount)
            }
        }

    @Test
    fun teamDecisionIsIndependentOfAttendanceStatus() =
        runTest {
            for (decision in AttendanceStaffDecision.entries) {
                client(historyBody("UNDECIDED", "\"$decision\"")).use {
                    val entry = repository(it).fetchHistory().items.single()
                    assertEquals(AttendanceResultStatus.UNDECIDED, entry.status)
                    assertEquals(decision, entry.staffDecision)
                }
            }
        }

    @Test
    fun malformedAndUnknownHistoryNeverBecomeEmptyOrAbsent() =
        runTest {
            val malformed =
                listOf(
                    "{}",
                    historyBody("FUTURE_STATUS"),
                    historyBody("PRESENT", "\"UNKNOWN_DECISION\""),
                    historyBody(
                        "PRESENT",
                    ).replace("\"startedSessionCount\":1", "\"startedSessionCount\":13"),
                    historyBody("PRESENT").replace("\"sessionId\":1", "\"sessionId\":0"),
                )
            for (body in malformed) {
                client(
                    body,
                ).use { assertTrue(runCatching { repository(it).fetchHistory() }.isFailure) }
            }
        }

    @Test
    fun negativeAndIncompleteScoresAreRejected() =
        runTest {
            for (body in listOf(
                "{}",
                """{"generationId":12,"totalScore":-1,"attendanceCount":0,
                "lateCount":0,"absentCount":0,"earlyLeaveCount":0}""",
            )) {
                client(
                    body,
                ).use { assertTrue(runCatching { repository(it).fetchScore() }.isFailure) }
            }
        }

    @Test
    fun cancellationPropagates() =
        runTest {
            configuredClient(MockEngine { throw CancellationException("cancelled") }).use {
                val error = runCatching { repository(it).fetchHistory() }.exceptionOrNull()
                assertTrue(error is CancellationException)
            }
        }

    private fun repository(client: HttpClient): AttendanceReadRepositoryImpl =
        AttendanceReadRepositoryImpl(AttendanceReadRemoteDataSource(client))

    private fun client(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ) = configuredClient(
        MockEngine {
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        },
    )

    private fun configuredClient(engine: MockEngine) =
        HttpClient(engine) {
            install(DefaultRequest) { url("https://api.example.test/api/") }
            install(ContentNegotiation) { json() }
            expectSuccess = true
        }

    private fun historyBody(
        status: String,
        decision: String = "null",
    ) = """{"summary":{"totalSessionCount":12,"startedSessionCount":1},
        "items":[{"sessionId":1,"sessionName":"세션","attendanceStatus":"$status",
        "staffDecisionStatus":$decision}],"nextCursor":null}"""
}
