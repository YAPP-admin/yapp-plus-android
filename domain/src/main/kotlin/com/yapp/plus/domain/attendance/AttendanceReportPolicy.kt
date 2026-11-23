package com.yapp.plus.domain.attendance

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object AttendanceReportPolicy {
    const val MAX_REASON_LENGTH = 30

    fun isReasonValid(text: String): Boolean = text.length <= MAX_REASON_LENGTH && text.isNotBlank()

    fun canSubmit(
        sessionDate: LocalDate?,
        startTime: LocalTime?,
        now: Instant,
    ): Boolean {
        if (sessionDate == null || startTime == null) return false
        val startsAt = sessionDate.atTime(startTime).atZone(ZoneId.of("Asia/Seoul")).toInstant()
        return now.isBefore(startsAt)
    }
}
