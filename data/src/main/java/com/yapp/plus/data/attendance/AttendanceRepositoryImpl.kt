package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository

class AttendanceRepositoryImpl(
    private val dataSource: AttendanceDataSource,
) : AttendanceRepository {
    override suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    ) = dataSource.verifyAttendance(sessionId, code)

    override suspend fun submitReport(
        type: AttendanceReportType,
        reason: String,
    ) = dataSource.submitReport(type, reason)
}
