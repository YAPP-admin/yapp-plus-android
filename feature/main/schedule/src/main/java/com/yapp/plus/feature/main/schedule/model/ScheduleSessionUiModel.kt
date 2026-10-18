package com.yapp.plus.feature.main.schedule.model

data class ScheduleSessionUiModel(
    val dateLabel: String,
    val name: String,
    val location: String?,
    val timeLabel: String,
    val type: ScheduleTypeUiModel,
    val status: ScheduleStatusUiModel,
)
