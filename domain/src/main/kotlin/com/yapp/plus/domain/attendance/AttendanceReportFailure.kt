package com.yapp.plus.domain.attendance

enum class AttendanceReportFailure {
    NOT_ELIGIBLE,
    SESSION_NOT_FOUND,
    UNAVAILABLE,
    SESSION_CHANGED,
    INVALID_REPORT,
    UNKNOWN,
    ;

    companion object {
        fun fromRawCode(code: String?): AttendanceReportFailure? =
            when (code) {
                "ATTENDANCE_REPORT_NOT_ELIGIBLE" -> NOT_ELIGIBLE
                "ATTENDANCE_SESSION_NOT_FOUND" -> SESSION_NOT_FOUND
                "ATTENDANCE_REPORT_UNAVAILABLE" -> UNAVAILABLE
                "ATTENDANCE_REPORT_SESSION_CHANGED", "ATTENDANCE_REPORT_SESSION_CONFLICT" -> SESSION_CHANGED
                "INVALID_ATTENDANCE_REPORT" -> INVALID_REPORT
                null -> null
                else -> UNKNOWN
            }
    }
}
