package com.yapp.plus.data.session

import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.IOException
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
                        session =
                            CurrentWeekSessionData(
                                sessionId = 12,
                                name = "세션",
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                type = SessionType.TEAM,
                                location = null,
                                attendanceStatus = status,
                            ),
                    )
                val expected =
                    CurrentWeekSchedule(
                        weekStartDate = date.minusDays(6),
                        weekEndDate = date,
                        session =
                            CurrentWeekSession(
                                sessionId = 12,
                                name = "세션",
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                type = SessionType.TEAM,
                                location = null,
                                attendanceStatus = status,
                            ),
                    )
                assertEquals(
                    YappResult.Success(expected),
                    repository.fetchCurrentWeekSchedule(),
                )
            }
            source.currentWeekSchedule =
                CurrentWeekScheduleData(
                    weekStartDate = date,
                    weekEndDate = date.plusDays(6),
                    session = null,
                )
            val emptyWeek =
                CurrentWeekSchedule(
                    weekStartDate = date,
                    weekEndDate = date.plusDays(6),
                    session = null,
                )
            assertEquals(YappResult.Success(emptyWeek), repository.fetchCurrentWeekSchedule())
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
                YappResult.Success(CurrentGenerationSchedule(31, 31, expected)),
                repository.fetchCurrentGenerationSchedule(),
            )
            source.currentGenerationSchedule = CurrentGenerationScheduleData(31, 31, emptyList())
            assertEquals(
                YappResult.Success(CurrentGenerationSchedule(31, 31, emptyList())),
                repository.fetchCurrentGenerationSchedule(),
            )
        }

    @Test
    fun `both lookups distinguish missing generation from unexpected source errors`() =
        runTest {
            val source = FakeSessionScheduleDataSource()
            val repository = SessionScheduleRepositoryImpl(source)
            val failures =
                listOf(
                    NoActiveGenerationException() to ScheduleFailure.NoActiveGeneration,
                    IllegalStateException("unavailable") to ScheduleFailure.Unavailable,
                )
            for ((failure, reason) in failures) {
                source.failure = failure
                val expected = YappResult.Failure(reason)
                assertEquals(expected, repository.fetchCurrentWeekSchedule())
                assertEquals(expected, repository.fetchCurrentGenerationSchedule())
            }
        }

    @Test
    fun `both result lookups return unavailable on a network failure`() =
        runTest {
            val source = FakeSessionScheduleDataSource().apply { failure = IOException("offline") }
            val repository = SessionScheduleRepositoryImpl(source)
            val expected = YappResult.Failure(ScheduleFailure.Unavailable)
            assertEquals(expected, repository.fetchCurrentWeekSchedule())
            assertEquals(expected, repository.fetchCurrentGenerationSchedule())
        }

    @Test
    fun `both result lookups propagate the original cancellation`() =
        runTest {
            val cancellation = CancellationException()
            val source = FakeSessionScheduleDataSource().apply { failure = cancellation }
            val repository = SessionScheduleRepositoryImpl(source)
            assertSame(
                cancellation,
                runCatching { repository.fetchCurrentWeekSchedule() }.exceptionOrNull(),
            )
            assertSame(
                cancellation,
                runCatching { repository.fetchCurrentGenerationSchedule() }.exceptionOrNull(),
            )
        }
}
