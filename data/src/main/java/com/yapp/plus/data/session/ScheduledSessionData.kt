package com.yapp.plus.data.session

import com.yapp.plus.domain.session.SessionType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class ScheduledSessionData(
    val sessionId: Long,
    val name: String,
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val location: String?,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val type: SessionType,
)
