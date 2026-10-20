package com.yapp.plus.data.session

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class SessionScheduleRepositoryImplTest {
    private val date = LocalDate.of(2026, 10, 24)
    private val startTime = LocalTime.of(14, 0)
    private val endTime = LocalTime.of(18, 0)

    @Test
    fun `injected source maps every weekly field and preserves missing session`() =
        runTest {
            val source = FakeSessionScheduleDataSource()
            val repository = SessionScheduleRepositoryImpl(source)
            for (status in AttendanceStatus.entries) {
                source.currentWeekSchedule =
                    CurrentWeekScheduleData(
                        weekStartDate = date.minusDays(6),
                        weekEndDate = date,
                        session = CurrentWeekSessionData(12, "세션", date, startTime, null, status),
                    )
                assertEquals(
                    CurrentWeekSchedule(
                        weekStartDate = date.minusDays(6),
                        weekEndDate = date,
                        session = CurrentWeekSession(12, "세션", date, startTime, null, status),
                    ),
                    repository.fetchCurrentWeekSchedule(),
                )
            }
            source.currentWeekSchedule =
                CurrentWeekScheduleData(
                    weekStartDate = date,
                    weekEndDate = date.plusDays(6),
                    session = null,
                )
            assertEquals(
                CurrentWeekSchedule(
                    weekStartDate = date,
                    weekEndDate = date.plusDays(6),
                    session = null,
                ),
                repository.fetchCurrentWeekSchedule(),
            )
        }

    @Test
    fun `injected source maps generation and ordered sessions with nullable locations`() =
        runTest {
            val source = FakeSessionScheduleDataSource()
            val sessions =
                SessionType.entries.mapIndexed { index, type ->
                    ScheduledSessionData(
                        sessionId = index.toLong() + 1,
                        name = "세션 $index",
                        date = date,
                        dayOfWeek = DayOfWeek.SATURDAY,
                        location = if (index == 0) "서울" else null,
                        startTime = startTime,
                        endTime = endTime,
                        type = type,
                    )
                }
            source.currentGenerationSchedule = CurrentGenerationScheduleData(31, 31, sessions)
            val expected =
                sessions.map {
                    ScheduledSession(
                        sessionId = it.sessionId,
                        name = it.name,
                        date = date,
                        dayOfWeek = DayOfWeek.SATURDAY,
                        location = it.location,
                        startTime = startTime,
                        endTime = endTime,
                        type = it.type,
                    )
                }
            val repository = SessionScheduleRepositoryImpl(source)
            assertEquals(
                CurrentGenerationSchedule(31, 31, expected),
                repository.fetchCurrentGenerationSchedule(),
            )
            source.currentGenerationSchedule = CurrentGenerationScheduleData(31, 31, emptyList())
            assertEquals(
                emptyList<ScheduledSession>(),
                repository.fetchCurrentGenerationSchedule().sessions,
            )
        }

    @Test
    fun `source failures and cancellation propagate from both lookups`() =
        runTest {
            val source = FakeSessionScheduleDataSource()
            val repository = SessionScheduleRepositoryImpl(source)
            for (failure in listOf(IllegalStateException("unavailable"), CancellationException())) {
                source.failure = failure
                assertSame(
                    failure,
                    runCatching { repository.fetchCurrentWeekSchedule() }.exceptionOrNull(),
                )
                assertSame(
                    failure,
                    runCatching { repository.fetchCurrentGenerationSchedule() }.exceptionOrNull(),
                )
            }
        }
}
