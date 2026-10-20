package com.yapp.plus.data.session

import com.yapp.plus.core.network.BuildConfig
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.SessionType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class SessionScheduleRemoteDataSourceTest {
    @Test
    fun `weekly request maps dates status and optional session from YAPP response`() =
        runTest {
            val response =
                """{"weekStartDate":"2026-10-18","weekEndDate":"2026-10-24","session":{
                "sessionId":12,"name":"세션","date":"2026-10-24","startTime":"14:00",
                "attendanceStatus":"AVAILABLE"}}"""
            createClient(
                response = response,
                path = "sessions/current-week",
                example = BuildConfig.CURRENT_WEEK_MOCK_EXAMPLE,
            ).use {
                val result = SessionScheduleRemoteDataSource(it).fetchCurrentWeekSchedule()
                assertEquals(LocalDate.of(2026, 10, 18), result.weekStartDate)
                assertEquals(LocalDate.of(2026, 10, 24), result.weekEndDate)
                assertEquals(
                    CurrentWeekSessionData(
                        sessionId = 12,
                        name = "세션",
                        date = LocalDate.of(2026, 10, 24),
                        startTime = LocalTime.of(14, 0),
                        location = null,
                        attendanceStatus = AttendanceStatus.AVAILABLE,
                    ),
                    result.session,
                )
            }
            val emptyWeek = """{"weekStartDate":"2026-10-18","weekEndDate":"2026-10-24"}"""
            createClient(emptyWeek).use {
                val result = SessionScheduleRemoteDataSource(it).fetchCurrentWeekSchedule()
                assertNull(result.session)
            }
        }

    @Test
    fun `generation request maps every scheduled field from YAPP response`() =
        runTest {
            val response =
                """{"generationId":31,"generationNumber":31,"sessions":[{"sessionId":12,
                "name":"팀 세션","date":"2026-10-24","dayOfWeek":"SATURDAY","location":"서울",
                "startTime":"14:00","endTime":"18:00","type":"TEAM"}]}"""
            val path = "generations/current/sessions"
            createClient(
                response = response,
                path = path,
                example = BuildConfig.CURRENT_GENERATION_SESSIONS_MOCK_EXAMPLE,
            ).use {
                val expected =
                    ScheduledSessionData(
                        sessionId = 12,
                        name = "팀 세션",
                        date = LocalDate.of(2026, 10, 24),
                        dayOfWeek = DayOfWeek.SATURDAY,
                        location = "서울",
                        startTime = LocalTime.of(14, 0),
                        endTime = LocalTime.of(18, 0),
                        type = SessionType.TEAM,
                    )
                assertEquals(
                    CurrentGenerationScheduleData(31, 31, listOf(expected)),
                    SessionScheduleRemoteDataSource(it).fetchCurrentGenerationSchedule(),
                )
            }
        }

    @Test
    fun `HTTP errors and invalid response values propagate`() =
        runTest {
            createClient("", status = HttpStatusCode.ServiceUnavailable).use {
                val source = SessionScheduleRemoteDataSource(it)
                val weekFailure =
                    runCatching { source.fetchCurrentWeekSchedule() }.exceptionOrNull()
                val generationFailure =
                    runCatching { source.fetchCurrentGenerationSchedule() }.exceptionOrNull()
                assertTrue(weekFailure is ResponseException)
                assertTrue(generationFailure is ResponseException)
            }
            createClient("""{"weekStartDate":"invalid","weekEndDate":"2026-10-24"}""").use {
                val result =
                    runCatching { SessionScheduleRemoteDataSource(it).fetchCurrentWeekSchedule() }
                assertTrue(result.isFailure)
            }
            val unknownType =
                """{"generationId":31,"generationNumber":31,"sessions":[{"sessionId":12,
                "name":"세션","date":"2026-10-24","dayOfWeek":"SATURDAY","startTime":"14:00",
                "endTime":"18:00","type":"UNKNOWN"}]}"""
            createClient(unknownType).use {
                val source = SessionScheduleRemoteDataSource(it)
                val result = runCatching { source.fetchCurrentGenerationSchedule() }
                assertTrue(result.isFailure)
            }
        }

    private fun createClient(
        response: String,
        path: String? = null,
        example: String? = null,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): HttpClient =
        HttpClient(
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                if (path != null) {
                    assertEquals("/api/$path", request.url.encodedPath)
                    val prefer = example?.takeIf { BuildConfig.MOCK_API_ENABLED && it.isNotBlank() }
                    assertEquals(prefer?.let { "example=$it" }, request.headers[HttpHeaders.Prefer])
                }
                respond(
                    response,
                    status,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        ) {
            expectSuccess = true
            install(DefaultRequest) { url("https://api.example.test/api/") }
            install(ContentNegotiation) { json() }
        }
}
