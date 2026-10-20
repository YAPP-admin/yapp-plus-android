package com.yapp.plus.data.attendance

import kotlinx.serialization.Serializable

@Serializable
internal class SubmitAttendanceReportRequest(
    val type: String,
    val reason: String,
)
