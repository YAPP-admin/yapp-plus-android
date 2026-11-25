package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val address: String? = null,
    val coordinates: SessionCoordinates? = null,
    val endTime: LocalTime? = null,
    val type: SessionType? = null,
)

internal val homeSessionStartFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d'일' HH:mm", Locale.KOREAN)
