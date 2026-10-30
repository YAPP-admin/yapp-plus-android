package com.yapp.plus.data.attendance.read

data class AttendanceHistoryEntryData(
    val sessionId: Long,
    val sessionName: String,
    val attendanceStatus: String,
    val staffDecisionStatus: String?,
)
