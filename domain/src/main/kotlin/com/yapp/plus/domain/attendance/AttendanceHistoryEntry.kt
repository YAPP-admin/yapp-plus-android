package com.yapp.plus.domain.attendance

data class AttendanceHistoryEntry(
    val sessionId: Long,
    val sessionName: String,
    val status: AttendanceResultStatus,
    val staffDecision: AttendanceStaffDecision?,
)
