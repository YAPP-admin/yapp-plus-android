package com.yapp.plus.data.attendance

import kotlinx.serialization.Serializable

@Serializable
internal class AttendanceReportResponse(
    val sessionId: Long,
    val submittedAt: String,
)
