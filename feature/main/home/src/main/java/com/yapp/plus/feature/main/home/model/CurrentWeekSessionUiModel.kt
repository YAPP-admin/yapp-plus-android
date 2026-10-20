package com.yapp.plus.feature.main.home.model

import java.time.LocalDate
import java.time.LocalTime

data class CurrentWeekSessionUiModel(
    val weekStartDateLabel: String,
    val weekEndDateLabel: String,
    val dateLabel: String,
    val name: String,
    val startTimeLabel: String,
    val location: String?,
    val attendanceStatus: AttendanceStatusUiModel,
    val sessionId: Long = 0,
    val weekStartDate: LocalDate? = null,
    val sessionDate: LocalDate? = null,
    val startTime: LocalTime? = null,
)
