package com.yapp.plus.feature.main.home.model

import com.yapp.plus.core.ui.formatter.AppDateFormatter
import com.yapp.plus.core.ui.mapper.toUiModel
import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.domain.session.CurrentWeekSchedule

internal object CurrentWeekSessionUiMapper {
    fun toUiState(schedule: CurrentWeekSchedule): AttendanceUiState<CurrentWeekSessionUiModel> {
        val session = schedule.session ?: return AttendanceUiState.Empty

        return AttendanceUiState.Content(
            CurrentWeekSessionUiModel(
                weekStartDateLabel = schedule.weekStartDate.format(AppDateFormatter.monthDay),
                weekEndDateLabel = schedule.weekEndDate.format(AppDateFormatter.monthDay),
                dateLabel = session.date.format(AppDateFormatter.monthDayDayOfWeek),
                name = session.name,
                startTimeLabel = session.startTime.format(AppDateFormatter.timeAmPmHour),
                location = session.location,
                attendanceStatus = session.attendanceStatus.toUiModel(),
                sessionId = session.sessionId,
                weekStartDate = schedule.weekStartDate,
                sessionDate = session.date,
                startTime = session.startTime,
                address = session.locationDetails?.roadAddress?.takeUnless(String::isBlank),
                coordinates = session.locationDetails?.coordinates,
                endTime = session.endTime,
                type = session.type,
            ),
        )
    }
}
