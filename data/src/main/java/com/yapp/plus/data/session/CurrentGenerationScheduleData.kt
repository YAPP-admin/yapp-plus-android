package com.yapp.plus.data.session

data class CurrentGenerationScheduleData(
    val generationId: Long,
    val generationNumber: Int,
    val sessions: List<ScheduledSessionData>,
)
