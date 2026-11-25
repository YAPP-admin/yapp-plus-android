package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.attendance.AttendanceReportPolicy
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

private val reportTipStartTime = LocalTime.of(9, 0)
private val seoulZone = ZoneId.of("Asia/Seoul")

internal fun shouldShowReportTip(
    sessionDate: LocalDate?,
    sessionStartTime: LocalTime?,
    now: Instant,
): Boolean {
    val date = sessionDate ?: return false
    if (!AttendanceReportPolicy.canSubmit(date, sessionStartTime, now)) return false

    val reportTipStartsAt =
        date
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atTime(reportTipStartTime)
            .atZone(seoulZone)
            .toInstant()
    return !now.isBefore(reportTipStartsAt)
}
