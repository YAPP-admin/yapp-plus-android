package com.yapp.plus.data.attendance.read

import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceHistoryEntryResponse(
    val sessionId: Long,
    val sessionName: String,
    val attendanceStatus: String,
    val staffDecisionStatus: String?,
) {
    fun toData(): AttendanceHistoryEntryData =
        AttendanceHistoryEntryData(
            sessionId = sessionId,
            sessionName = sessionName,
            attendanceStatus = attendanceStatus,
            staffDecisionStatus = staffDecisionStatus,
        )
}
