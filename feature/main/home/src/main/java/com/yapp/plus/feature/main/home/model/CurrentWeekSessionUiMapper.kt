package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import java.time.format.DateTimeFormatter
import java.util.Locale

internal object CurrentWeekSessionUiMapper {
    fun toUiState(schedule: CurrentWeekSchedule): AttendanceUiState<CurrentWeekSessionUiModel> {
        val session = schedule.session ?: return AttendanceUiState.Empty

        return AttendanceUiState.Content(
            CurrentWeekSessionUiModel(
                weekStartDateLabel = schedule.weekStartDate.format(weekDateFormatter),
                weekEndDateLabel = schedule.weekEndDate.format(weekDateFormatter),
                dateLabel = session.date.format(sessionDateFormatter),
                name = session.name,
                startTimeLabel = session.startTime.format(timeFormatter),
                location = session.location,
                attendanceStatus = session.attendanceStatus.toUiModel(),
            ),
        )
    }

    private fun AttendanceStatus.toUiModel(): AttendanceStatusUiModel =
        when (this) {
            AttendanceStatus.NOT_ELIGIBLE -> AttendanceStatusUiModel.NotEligible
            AttendanceStatus.NOT_OPEN -> AttendanceStatusUiModel.NotOpen
            AttendanceStatus.AVAILABLE -> AttendanceStatusUiModel.Available
        }

    private val weekDateFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
    private val sessionDateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    private val timeFormatter = DateTimeFormatter.ofPattern("a h시", Locale.KOREAN)
}
