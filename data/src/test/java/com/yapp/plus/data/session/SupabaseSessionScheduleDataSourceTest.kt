package com.yapp.plus.data.session

import com.yapp.plus.data.supabase.SupabaseDataSourceException
import com.yapp.plus.data.supabase.SupabaseTestFixture
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.SessionType
import io.ktor.client.engine.mock.respond
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class SupabaseSessionScheduleDataSourceTest {
    @Test
    fun `week uses server Sunday to Saturday in Seoul and selects first ordered test session`() =
        runTest {
            SupabaseTestFixture(handler = { request ->
                when (request.url.encodedPath) {
                    "/rest/v1/mvp_context" -> respond(context)
                    else -> {
                        assertEquals("/rest/v1/mvp_session_schedule", request.url.encodedPath)
                        assertEquals("eq.true", request.url.parameters["is_test"])
                        val expectedOrder = "starts_at.asc,ends_at.asc,id.asc"
                        assertEquals(expectedOrder, request.url.parameters["order"])
                        assertEquals("1", request.url.parameters["limit"])
                        assertEquals(
                            listOf("gte.2026-10-17T15:00:00Z", "lt.2026-10-24T15:00:00Z"),
                            request.url.parameters.getAll("starts_at"),
                        )
                        respond("[$session]")
                    }
                }
            }).use {
                val result =
                    SessionScheduleRepositoryImpl(SupabaseSessionScheduleDataSource(it.client))
                        .fetchCurrentWeekSchedule()
                assertEquals(LocalDate.of(2026, 10, 18), result.weekStartDate)
                assertEquals(LocalDate.of(2026, 10, 24), result.weekEndDate)
                assertEquals(LocalDate.of(2026, 10, 24), result.session?.date)
                assertEquals(LocalTime.of(0, 30), result.session?.startTime)
                assertEquals(AttendanceStatus.AVAILABLE, result.session?.attendanceStatus)
                assertNull(result.session?.location)
            }
        }

    @Test
    fun `generation maps missing metadata to test sentinel and preserves session fields`() =
        runTest {
            SupabaseTestFixture(handler = { request ->
                if (request.url.encodedPath.endsWith("mvp_context")) {
                    respond(context)
                } else {
                    assertNull(request.url.parameters["starts_at"])
                    assertNull(request.url.parameters["limit"])
                    respond("[$session]")
                }
            }).use {
                val result =
                    SessionScheduleRepositoryImpl(SupabaseSessionScheduleDataSource(it.client))
                        .fetchCurrentGenerationSchedule()
                assertEquals(0L, result.generationId)
                assertEquals(0, result.generationNumber)
                val session = result.sessions.single()
                assertEquals(12L, session.sessionId)
                assertEquals("[더미] 세션", session.name)
                assertEquals(DayOfWeek.SATURDAY, session.dayOfWeek)
                assertEquals(LocalTime.of(2, 30), session.endTime)
                assertEquals(SessionType.TEAM, session.type)
            }
        }

    @Test
    fun `empty schedules are valid but missing context and invalid row values fail`() =
        runTest {
            SupabaseTestFixture(handler = { request ->
                respond(if (request.url.encodedPath.endsWith("mvp_context")) context else "[]")
            }).use {
                val source = SupabaseSessionScheduleDataSource(it.client)
                assertNull(source.fetchCurrentWeekSchedule().session)
                assertTrue(source.fetchCurrentGenerationSchedule().sessions.isEmpty())
            }
            for (body in listOf("[]", "{}", context.replace("Asia/Seoul", "UTC"))) {
                SupabaseTestFixture(handler = { respond(body) }).use {
                    val failure =
                        runCatching {
                            SupabaseSessionScheduleDataSource(it.client).fetchCurrentWeekSchedule()
                        }.exceptionOrNull()
                    assertTrue(failure is SupabaseDataSourceException)
                }
            }
            val invalidRows =
                listOf(
                    session.replace("TEAM", "UNKNOWN"),
                    session.replace("AVAILABLE", "UNKNOWN"),
                    session.replace("2026-10-23T15:30:00+00:00", "invalid"),
                )
            for (body in invalidRows) {
                SupabaseTestFixture(handler = { request ->
                    val isContext = request.url.encodedPath.endsWith("mvp_context")
                    val response = if (isContext) context else "[$body]"
                    respond(response)
                }).use {
                    val failure =
                        runCatching {
                            SupabaseSessionScheduleDataSource(it.client).fetchCurrentWeekSchedule()
                        }.exceptionOrNull()
                    assertTrue(failure is SupabaseDataSourceException)
                }
            }
        }

    private val context =
        """[{"generation_id":null,"generation_number":null,"time_zone":"Asia/Seoul",
        "server_date":"2026-10-20","week_start_date":"2026-10-18","week_end_date":"2026-10-24"}]"""
    private val session =
        """{"id":12,"name":"[더미] 세션","starts_at":"2026-10-23T15:30:00+00:00",
        "ends_at":"2026-10-23T17:30:00+00:00","session_type":"TEAM","is_test":true,
        "attendance_status":"AVAILABLE","checked_in_at":null}"""
}
