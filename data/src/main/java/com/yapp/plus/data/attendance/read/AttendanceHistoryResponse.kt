package com.yapp.plus.data.attendance.read

import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceHistoryResponse(
    val summary: AttendanceHistorySummaryResponse,
    val items: List<AttendanceHistoryEntryResponse>,
    val nextCursor: String?,
)
