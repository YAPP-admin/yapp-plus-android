package com.yapp.plus.feature.main.home.model

data class CurrentWeekSessionUiModel(
    val weekStartDateLabel: String,
    val weekEndDateLabel: String,
    val dateLabel: String,
    val name: String,
    val startTimeLabel: String,
    val location: String?,
    val attendanceStatus: AttendanceStatusUiModel,
)
