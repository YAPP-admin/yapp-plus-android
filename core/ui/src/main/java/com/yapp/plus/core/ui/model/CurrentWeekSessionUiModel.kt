package com.yapp.plus.core.ui.model

import com.yapp.plus.core.ui.formatter.AppDateFormatter
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val teamZone: ZoneId = ZoneId.of("Asia/Seoul")
private val teamTimeFormat: DateTimeFormatter get() = AppDateFormatter.timeHourMinute

data class CurrentWeekSessionUiModel(
    val weekStartDateLabel: String,
    val weekEndDateLabel: String,
    val dateLabel: String,
    val name: String,
    val startTimeLabel: String,
    val location: String,
    val attendanceStatus: AttendanceStatusUiModel,
    val sessionId: Long = 0,
    val weekStartDate: LocalDate? = null,
    val sessionDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val address: String? = null,
    val coordinates: SessionCoordinates? = null,
    val endTime: LocalTime? = null,
    val type: SessionType? = null,
) {
    fun canUploadTeamPhoto(now: Instant): Boolean {
        if (type != SessionType.TEAM) return false
        val date = sessionDate ?: return false
        val start = startTime ?: return false
        val end = endTime ?: return false
        val localNow = now.atZone(teamZone).toLocalDateTime()
        return (
            attendanceStatus == AttendanceStatusUiModel.Available ||
                attendanceStatus == AttendanceStatusUiModel.PendingApproval
        ) &&
            localNow >= date.atTime(start) &&
            localNow < date.atTime(end)
    }

    fun teamTimeLabel(): String =
        "${startTime?.format(teamTimeFormat).orEmpty()} - ${endTime?.format(teamTimeFormat).orEmpty()}"
}

val homeSessionStartFormat: DateTimeFormatter get() = AppDateFormatter.dayOfMonthTime
