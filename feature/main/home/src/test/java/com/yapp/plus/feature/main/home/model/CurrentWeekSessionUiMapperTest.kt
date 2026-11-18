package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionLocationDetails
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
                        location = "DMC 첨단산업센터",
                        attendanceStatus = AttendanceStatus.NOT_OPEN,
                        locationDetails =
                            SessionLocationDetails(
                                roadAddress = "서울특별시 마포구 월드컵북로 396",
                                detailAddress = "2층",
                                coordinates =
                                    SessionCoordinates(latitude = 37.58, longitude = 126.88),
                            ),
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
                    location = "DMC 첨단산업센터",
                    attendanceStatus = AttendanceStatusUiModel.NotOpen,
                    sessionId = 1,
                    weekStartDate = LocalDate.of(2026, 10, 18),
                    sessionDate = LocalDate.of(2026, 10, 24),
                    startTime = LocalTime.of(14, 0),
                    address = "서울특별시 마포구 월드컵북로 396 2층",
                    coordinates = SessionCoordinates(latitude = 37.58, longitude = 126.88),
                ),
            ),
            state,
        )
    }
}
