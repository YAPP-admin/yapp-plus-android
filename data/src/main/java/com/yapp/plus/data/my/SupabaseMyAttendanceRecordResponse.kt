package com.yapp.plus.data.my

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseMyAttendanceRecordResponse(
    @SerialName("session_id") val sessionId: Long,
    @SerialName("session_name") val sessionName: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    @SerialName("checked_in_at") val checkedInAt: String,
)
