package com.yapp.plus.domain.attendance

data class SubmittedAttendanceReport(
    val type: AttendanceReportType,
    val reason: String?,
)
