package com.yapp.plus.domain.attendance

data class AttendanceHistoryPage(
    val totalSessionCount: Int,
    val startedSessionCount: Int,
    val items: List<AttendanceHistoryEntry>,
    val nextCursor: String?,
)
