package com.yapp.plus.data.attendance

import kotlinx.serialization.Serializable

@Serializable
internal class VerifyAttendanceResponse(
    val verified: Boolean,
    val checkedInAt: String,
)
