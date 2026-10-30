package com.yapp.plus.data.attendance.read

import com.yapp.plus.domain.attendance.AttendanceHistoryEntry
import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.AttendanceStaffDecision

data class AttendanceHistoryEntryData(
    val sessionId: Long,
    val sessionName: String,
    val attendanceStatus: String,
    val staffDecisionStatus: String?,
) {
    internal fun toDomain(): AttendanceHistoryEntry {
        require(sessionId > 0 && sessionName.isNotBlank())
        return AttendanceHistoryEntry(
            sessionId = sessionId,
            sessionName = sessionName,
            status = AttendanceResultStatus.valueOf(attendanceStatus),
            staffDecision = staffDecisionStatus?.let(AttendanceStaffDecision::valueOf),
        )
    }
}
