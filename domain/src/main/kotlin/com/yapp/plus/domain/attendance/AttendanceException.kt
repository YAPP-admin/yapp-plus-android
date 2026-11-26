package com.yapp.plus.domain.attendance

class AttendanceException(
    val isInvalidCode: Boolean = false,
    val code: String? = null,
    val reportFailure: AttendanceReportFailure? = AttendanceReportFailure.fromRawCode(code),
) : Exception("Attendance request failed")
