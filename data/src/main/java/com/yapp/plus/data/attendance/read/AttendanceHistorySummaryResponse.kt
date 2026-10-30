package com.yapp.plus.data.attendance.read

import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceHistorySummaryResponse(
    val totalSessionCount: Int,
    val startedSessionCount: Int,
)
