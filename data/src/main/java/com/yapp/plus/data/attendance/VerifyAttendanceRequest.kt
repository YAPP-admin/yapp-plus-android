package com.yapp.plus.data.attendance

import kotlinx.serialization.Serializable

@Serializable
internal class VerifyAttendanceRequest(
    val sessionId: Long,
    val code: Int,
)
