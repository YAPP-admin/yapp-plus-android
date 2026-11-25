package com.yapp.plus.feature.main.schedule.model

import com.yapp.plus.core.ui.formatter.AppDateFormatter
import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

internal object SessionScheduleUiMapper {
    fun toUiState(
        schedule: CurrentGenerationSchedule,
        now: LocalDateTime = LocalDateTime.now(),
    ): ScheduleUiState<List<ScheduleSessionUiModel>> {
        if (schedule.sessions.isEmpty()) return ScheduleUiState.Empty

        return ScheduleUiState.Content(schedule.sessions.map { it.toUiModel(now) })
    }

    private fun ScheduledSession.toUiModel(now: LocalDateTime): ScheduleSessionUiModel =
        ScheduleSessionUiModel(
            dateLabel =
                "${date.format(AppDateFormatter.monthDayDot)} " +
                    "(${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)})",
            name = name,
            location = location?.takeUnless(String::isBlank),
            timeLabel = "${startTime.format(
                AppDateFormatter.timeAmPmHour,
            )} ~ ${endTime.format(AppDateFormatter.timeAmPmHour)}",
            type = type.toUiModel(),
            status = statusAt(now),
            address = locationDetails?.addressLine,
            sessionId = sessionId,
        )

    private fun ScheduledSession.statusAt(now: LocalDateTime): ScheduleStatusUiModel {
        val today = now.toLocalDate()
        return when {
            date.isBefore(today) -> ScheduleStatusUiModel.Past
            date.isAfter(today) -> ScheduleStatusUiModel.Upcoming
            !endTime.isAfter(now.toLocalTime()) -> ScheduleStatusUiModel.Past
            else -> ScheduleStatusUiModel.Today
        }
    }

    private fun SessionType.toUiModel(): ScheduleTypeUiModel =
        when (this) {
            SessionType.REGULAR -> ScheduleTypeUiModel.Regular
            SessionType.TEAM -> ScheduleTypeUiModel.Team
        }
}
