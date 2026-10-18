package com.yapp.plus.domain.session

import java.time.LocalDate
import java.time.LocalTime

data class CurrentWeekSession(
    val sessionId: Long,
    val name: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val location: String?,
    val attendanceStatus: AttendanceStatus,
)
