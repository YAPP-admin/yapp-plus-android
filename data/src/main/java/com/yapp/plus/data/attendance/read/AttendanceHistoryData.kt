package com.yapp.plus.data.attendance.read

data class AttendanceHistoryData(
    val totalSessionCount: Int,
    val startedSessionCount: Int,
    val items: List<AttendanceHistoryEntryData>,
    val nextCursor: String?,
)
