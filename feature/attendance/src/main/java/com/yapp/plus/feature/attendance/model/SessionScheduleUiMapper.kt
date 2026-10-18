package com.yapp.plus.feature.attendance.model

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

internal object SessionScheduleUiMapper {
    fun toCurrentWeekUiState(
        schedule: CurrentWeekSchedule,
    ): AttendanceUiState<CurrentWeekSessionUiModel> {
        val session = schedule.session ?: return AttendanceUiState.Empty

        return AttendanceUiState.Content(
            CurrentWeekSessionUiModel(
                weekStartDateLabel = schedule.weekStartDate.format(weekDateFormatter),
                weekEndDateLabel = schedule.weekEndDate.format(weekDateFormatter),
                dateLabel = session.date.format(homeSessionDateFormatter),
                name = session.name,
                startTimeLabel = session.startTime.format(timeFormatter),
                location = session.location,
                attendanceStatus = session.attendanceStatus.toUiModel(),
            ),
        )
    }

    fun toScheduleUiState(
        schedule: CurrentGenerationSchedule,
        now: LocalDateTime = LocalDateTime.now(),
    ): AttendanceUiState<List<AttendanceScheduleSessionUiModel>> {
        if (schedule.sessions.isEmpty()) return AttendanceUiState.Empty

        return AttendanceUiState.Content(schedule.sessions.map { it.toUiModel(now) })
    }

    private fun ScheduledSession.toUiModel(now: LocalDateTime): AttendanceScheduleSessionUiModel =
        AttendanceScheduleSessionUiModel(
            dateLabel =
                "${date.format(scheduleDateFormatter)} " +
                    "(${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)})",
            name = name,
            location = location,
            timeLabel = "${startTime.format(timeFormatter)} ~ ${endTime.format(timeFormatter)}",
            type = type.toUiModel(),
            status = statusAt(now),
        )

    private fun ScheduledSession.statusAt(now: LocalDateTime): AttendanceScheduleStatusUiModel {
        val today = now.toLocalDate()
        return when {
            date.isBefore(today) -> AttendanceScheduleStatusUiModel.Past
            date.isAfter(today) -> AttendanceScheduleStatusUiModel.Upcoming
            !endTime.isAfter(now.toLocalTime()) -> AttendanceScheduleStatusUiModel.Past
            else -> AttendanceScheduleStatusUiModel.Today
        }
    }

    private fun AttendanceStatus.toUiModel(): AttendanceStatusUiModel =
        when (this) {
            AttendanceStatus.NOT_ELIGIBLE -> AttendanceStatusUiModel.NotEligible
            AttendanceStatus.NOT_OPEN -> AttendanceStatusUiModel.NotOpen
            AttendanceStatus.AVAILABLE -> AttendanceStatusUiModel.Available
        }

    private fun SessionType.toUiModel(): AttendanceScheduleTypeUiModel =
        when (this) {
            SessionType.REGULAR -> AttendanceScheduleTypeUiModel.Regular
            SessionType.TEAM -> AttendanceScheduleTypeUiModel.Team
        }

    private val weekDateFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
    private val homeSessionDateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    private val scheduleDateFormatter = DateTimeFormatter.ofPattern("M.d", Locale.KOREAN)
    private val timeFormatter = DateTimeFormatter.ofPattern("a h시", Locale.KOREAN)
}
