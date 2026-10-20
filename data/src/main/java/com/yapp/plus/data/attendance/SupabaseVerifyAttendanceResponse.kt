package com.yapp.plus.data.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseVerifyAttendanceResponse(
    @SerialName("session_id") val sessionId: Long,
    val verified: Boolean,
    @SerialName("checked_in_at") val checkedInAt: String,
)
