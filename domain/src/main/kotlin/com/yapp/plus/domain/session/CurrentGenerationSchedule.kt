package com.yapp.plus.domain.session

data class CurrentGenerationSchedule(
    val generationId: Long,
    val generationNumber: Int,
    val sessions: List<ScheduledSession>,
)
