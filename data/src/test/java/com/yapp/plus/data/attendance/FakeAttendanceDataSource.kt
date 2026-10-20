package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceReportType

class FakeAttendanceDataSource : AttendanceDataSource {
    var verifiedSessionId: Long? = null
    var verifiedCode: Int? = null
    var reportType: AttendanceReportType? = null
    var reportReason: String? = null
    var reportSessionId: Long = 0
    var failure: Throwable? = null

    override suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    ) {
        failure?.let { throw it }
        verifiedSessionId = sessionId
        verifiedCode = code
    }

    override suspend fun submitReport(
        type: AttendanceReportType,
        reason: String,
    ): Long {
        failure?.let { throw it }
        reportType = type
        reportReason = reason
        return reportSessionId
    }
}
