package com.yapp.plus.data.session

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.SessionType
import java.time.LocalDate
import java.time.LocalTime

data class CurrentWeekSessionData(
    val sessionId: Long,
    val name: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val type: SessionType,
    val location: String?,
    val attendanceStatus: AttendanceStatus,
    val locationDetails: SessionLocationData? = null,
)
