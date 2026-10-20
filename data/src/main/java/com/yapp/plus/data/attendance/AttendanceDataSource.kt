package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceReportType

interface AttendanceDataSource {
    suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    )

    suspend fun submitReport(
        type: AttendanceReportType,
        reason: String,
    ): Long
}
