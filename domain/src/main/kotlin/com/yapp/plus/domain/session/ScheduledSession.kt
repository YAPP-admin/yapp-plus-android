package com.yapp.plus.domain.session

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class ScheduledSession(
    val sessionId: Long,
    val name: String,
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val location: String?,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val type: SessionType,
)
