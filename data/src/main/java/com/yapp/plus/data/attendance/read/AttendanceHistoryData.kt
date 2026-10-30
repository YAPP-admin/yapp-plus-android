package com.yapp.plus.data.attendance.read

import com.yapp.plus.domain.attendance.AttendanceHistoryPage

data class AttendanceHistoryData(
    val totalSessionCount: Int,
    val startedSessionCount: Int,
    val items: List<AttendanceHistoryEntryData>,
    val nextCursor: String?,
) {
    internal fun toDomain(): AttendanceHistoryPage {
        require(totalSessionCount >= 0 && startedSessionCount in 0..totalSessionCount)
        require(nextCursor == null || nextCursor.isNotBlank())
        return AttendanceHistoryPage(
            totalSessionCount = totalSessionCount,
            startedSessionCount = startedSessionCount,
            items = items.map { it.toDomain() },
            nextCursor = nextCursor,
        )
    }
}
