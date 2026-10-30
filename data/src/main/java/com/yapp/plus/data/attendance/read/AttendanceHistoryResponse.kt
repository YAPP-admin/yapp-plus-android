package com.yapp.plus.data.attendance.read

import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceHistoryResponse(
    val summary: AttendanceHistorySummaryResponse,
    val items: List<AttendanceHistoryEntryResponse>,
    val nextCursor: String?,
) {
    fun toData(): AttendanceHistoryData =
        AttendanceHistoryData(
            totalSessionCount = summary.totalSessionCount,
            startedSessionCount = summary.startedSessionCount,
            items = items.map { it.toData() },
            nextCursor = nextCursor,
        )
}
