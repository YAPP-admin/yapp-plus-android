package com.yapp.plus.data.session

import com.yapp.plus.core.network.BuildConfig
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionLocationDetails
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
                "endTime":"18:00:00","type":"TEAM","attendanceStatus":"AVAILABLE"}}"""
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
                        endTime = LocalTime.of(18, 0),
                        type = SessionType.TEAM,
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
    fun `weekly response maps checked in and pending approval into domain states`() =
        runTest {
            val statuses =
                mapOf(
                    "CHECKED_IN" to AttendanceStatus.CHECKED_IN,
                    "PENDING_APPROVAL" to AttendanceStatus.PENDING_APPROVAL,
                )
            for ((wireStatus, domainStatus) in statuses) {
                val response =
                    """{"weekStartDate":"2026-10-18","weekEndDate":"2026-10-24","session":{
                    "sessionId":12,"name":"세션","date":"2026-10-24","startTime":"14:00",
                    "endTime":"18:00","type":"TEAM","attendanceStatus":"$wireStatus"}}"""
                createClient(response).use { client ->
                    val repository =
                        SessionScheduleRepositoryImpl(SessionScheduleRemoteDataSource(client))
                    val session = requireNotNull(repository.fetchCurrentWeekSchedule().session)
                    assertEquals(domainStatus, session.attendanceStatus)
                }
            }
        }

    @Test
    fun `weekly request carries the registered venue and full coordinate precision`() =
        runTest {
            val response =
                """{"weekStartDate":"2026-10-18","weekEndDate":"2026-10-24","session":{
                "sessionId":12,"name":"정규 세션","date":"2026-10-24","startTime":"14:00",
                "endTime":"18:30","type":"REGULAR","attendanceStatus":"AVAILABLE",
                "location":"DMC 첨단산업센터",
                "roadAddress":"서울특별시 마포구 월드컵북로 396",
                "latitude":37.5823741,"longitude":126.8864512,"detailAddress":"2층"}}"""
            createClient(response).use {
                val repository = SessionScheduleRepositoryImpl(SessionScheduleRemoteDataSource(it))
                val session = requireNotNull(repository.fetchCurrentWeekSchedule().session)
                assertEquals(SessionType.REGULAR, session.type)
                assertEquals(LocalTime.of(18, 30), session.endTime)
                assertEquals("DMC 첨단산업센터", session.location)
                assertEquals(
                    SessionLocationDetails(
                        roadAddress = "서울특별시 마포구 월드컵북로 396",
                        detailAddress = "2층",
                        coordinates =
                            SessionCoordinates(latitude = 37.5823741, longitude = 126.8864512),
                    ),
                    session.locationDetails,
                )
            }
        }

    @Test
    fun `generation request maps every scheduled field from YAPP response`() =
        runTest {
            val response =
                """{"generationId":31,"generationNumber":31,"sessions":[{"sessionId":12,
                "name":"팀 세션","date":"2026-10-24","dayOfWeek":"SATURDAY","location":"서울",
                "startTime":"14:00","endTime":"18:00","type":"TEAM","roadAddress":null,
                "detailAddress":null,"latitude":null,"longitude":null}]}"""
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
    fun `generation address remains usable without a complete valid coordinate pair`() =
        runTest {
            val items =
                listOf(
                    """"latitude":37.5823741,"longitude":126.8864512,"detailAddress":" 2층 """",
                    """"latitude":37.5823741,"longitude":null,"detailAddress":" " """,
                    """"latitude":91.0,"longitude":126.8864512""",
                    """"latitude":37.5823741,"longitude":181.0""",
                ).mapIndexed { index, fields ->
                    """{"sessionId":$index,"name":"세션","date":"2026-10-24",
                    "dayOfWeek":"SATURDAY","location":"DMC 첨단산업센터","startTime":"14:00",
                    "endTime":"18:00","type":"REGULAR","roadAddress":" 서울특별시 마포구 월드컵북로 396 ",
                    $fields}"""
                }.joinToString(prefix = "[", postfix = "]")
            val response = """{"generationId":31,"generationNumber":31,"sessions":$items}"""
            createClient(response).use {
                val repository = SessionScheduleRepositoryImpl(SessionScheduleRemoteDataSource(it))
                val sessions = repository.fetchCurrentGenerationSchedule().sessions
                val locations = sessions.map { requireNotNull(it.locationDetails) }
                val roadAddress = "서울특별시 마포구 월드컵북로 396"
                assertEquals("$roadAddress 2층", locations.first().addressLine)
                assertEquals(
                    SessionCoordinates(latitude = 37.5823741, longitude = 126.8864512),
                    locations.first().coordinates,
                )
                for (location in locations.drop(1)) {
                    assertEquals(roadAddress, location.addressLine)
                    assertNull(location.coordinates)
                }
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

    @Test
    fun onlyDocumentedInactiveGeneration404BecomesNormalAbsence() =
        runTest {
            for (code in listOf("CURRENT_GENERATION_NOT_FOUND", "OTHER_NOT_FOUND")) {
                createClient("""{"code":"$code"}""", status = HttpStatusCode.NotFound).use {
                    val source = SessionScheduleRemoteDataSource(it)
                    val errors =
                        listOf(
                            runCatching { source.fetchCurrentWeekSchedule() }.exceptionOrNull(),
                            runCatching { source.fetchCurrentGenerationSchedule() }.exceptionOrNull(),
                        )
                    for (error in errors) {
                        if (code == "CURRENT_GENERATION_NOT_FOUND") {
                            assertTrue(error is com.yapp.plus.domain.session.NoActiveGenerationException)
                        } else {
                            assertTrue(error is ResponseException)
                        }
                    }
                }
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
