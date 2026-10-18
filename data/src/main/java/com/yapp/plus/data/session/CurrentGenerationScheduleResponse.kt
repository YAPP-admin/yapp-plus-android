package com.yapp.plus.data.session

import kotlinx.serialization.Serializable

@Serializable
internal data class CurrentGenerationScheduleResponse(
    val generationId: Long,
    val generationNumber: Int,
    val sessions: List<ScheduledSessionResponse>,
)
