package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class CurrentWeekSessionUiMapperTest {
    @Test
    fun currentWeekWithoutSessionReturnsEmptyState() {
        val schedule =
            CurrentWeekSchedule(
                weekStartDate = LocalDate.of(2026, 10, 18),
                weekEndDate = LocalDate.of(2026, 10, 24),
                session = null,
            )

        assertEquals(AttendanceUiState.Empty, CurrentWeekSessionUiMapper.toUiState(schedule))
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

        val state = CurrentWeekSessionUiMapper.toUiState(schedule)

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
                    sessionId = 1,
                    weekStartDate = LocalDate.of(2026, 10, 18),
                    sessionDate = LocalDate.of(2026, 10, 24),
                    startTime = LocalTime.of(14, 0),
                ),
            ),
            state,
        )
    }
}
