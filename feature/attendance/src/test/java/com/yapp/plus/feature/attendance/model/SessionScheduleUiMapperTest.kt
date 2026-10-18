package com.yapp.plus.feature.attendance.model

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SessionScheduleUiMapperTest {
    @Test
    fun currentWeekWithoutSessionReturnsEmptyState() {
        val schedule =
            CurrentWeekSchedule(
                weekStartDate = LocalDate.of(2026, 10, 18),
                weekEndDate = LocalDate.of(2026, 10, 24),
                session = null,
            )

        assertEquals(
            AttendanceUiState.Empty,
            SessionScheduleUiMapper.toCurrentWeekUiState(schedule),
        )
    }

    @Test
    fun currentWeekMapsServerAttendanceStatusAndDateLabels() {
        val schedule =
            CurrentWeekSchedule(
                weekStartDate = LocalDate.of(2026, 10, 18),
                weekEndDate = LocalDate.of(2026, 10, 24),
                session =
                    CurrentWeekSession(
                        sessionId = 1,
                        name = "개발 세션",
                        date = LocalDate.of(2026, 10, 24),
                        startTime = LocalTime.of(14, 0),
                        location = null,
                        attendanceStatus = AttendanceStatus.NOT_OPEN,
                    ),
            )

        val state = SessionScheduleUiMapper.toCurrentWeekUiState(schedule)

        assertEquals(
            AttendanceUiState.Content(
                CurrentWeekSessionUiModel(
                    weekStartDateLabel = "10월 18일",
                    weekEndDateLabel = "10월 24일",
                    dateLabel = "10월 24일 (토)",
                    name = "개발 세션",
                    startTimeLabel = "오후 2시",
                    location = null,
                    attendanceStatus = AttendanceStatusUiModel.NotOpen,
                ),
            ),
            state,
        )
    }

    @Test
    fun emptyGenerationScheduleReturnsEmptyState() {
        val schedule =
            CurrentGenerationSchedule(
                generationId = 19,
                generationNumber = 19,
                sessions = emptyList(),
            )

        assertEquals(AttendanceUiState.Empty, SessionScheduleUiMapper.toScheduleUiState(schedule))
    }

    @Test
    fun scheduleStatusUsesSessionDateAndEndTime() {
        val now = LocalDateTime.of(2026, 10, 18, 12, 0)
        val schedule =
            CurrentGenerationSchedule(
                generationId = 19,
                generationNumber = 19,
                sessions =
                    listOf(
                        session(
                            date = LocalDate.of(2026, 10, 17),
                            endTime = LocalTime.of(18, 0),
                        ),
                        session(
                            date = LocalDate.of(2026, 10, 18),
                            endTime = LocalTime.of(11, 59),
                        ),
                        session(
                            date = LocalDate.of(2026, 10, 18),
                            endTime = LocalTime.of(18, 0),
                        ),
                        session(
                            date = LocalDate.of(2026, 10, 19),
                            endTime = LocalTime.of(18, 0),
                        ),
                    ),
            )

        val state = SessionScheduleUiMapper.toScheduleUiState(schedule, now)

        assertEquals(
            listOf(
                AttendanceScheduleStatusUiModel.Past,
                AttendanceScheduleStatusUiModel.Past,
                AttendanceScheduleStatusUiModel.Today,
                AttendanceScheduleStatusUiModel.Upcoming,
            ),
            (state as AttendanceUiState.Content).value.map { it.status },
        )
    }

    private fun session(
        date: LocalDate,
        endTime: LocalTime,
    ) = ScheduledSession(
        sessionId = 1,
        name = "세션",
        date = date,
        dayOfWeek = date.dayOfWeek,
        location = null,
        startTime = LocalTime.of(14, 0),
        endTime = endTime,
        type = SessionType.REGULAR,
    )
}
