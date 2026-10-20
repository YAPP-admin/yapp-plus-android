package com.yapp.plus.domain.attendance

interface AttendanceRepository {
    suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    )

    suspend fun submitReport(
        type: AttendanceReportType,
        reason: String,
    ): Long
}
