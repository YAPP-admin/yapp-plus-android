package com.yapp.plus.data.session

import kotlinx.serialization.Serializable

@Serializable
internal data class ScheduledSessionResponse(
    val sessionId: Long,
    val name: String,
    val date: String,
    val dayOfWeek: String,
    val location: String? = null,
    val startTime: String,
    val endTime: String,
    val type: String,
)
