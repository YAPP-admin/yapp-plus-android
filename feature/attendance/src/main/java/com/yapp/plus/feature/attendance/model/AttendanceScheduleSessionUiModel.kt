package com.yapp.plus.feature.attendance.model

data class AttendanceScheduleSessionUiModel(
    val dateLabel: String,
    val name: String,
    val location: String?,
    val timeLabel: String,
    val type: AttendanceScheduleTypeUiModel,
    val status: AttendanceScheduleStatusUiModel,
)
