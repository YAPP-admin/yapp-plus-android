package com.yapp.plus.domain.attendance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AttendanceReportFailureTest {
    @Test
    fun mapsRawCodesToAttendanceReportFailureCorrectly() {
        assertEquals(
            AttendanceReportFailure.NOT_ELIGIBLE,
            AttendanceReportFailure.fromRawCode("ATTENDANCE_REPORT_NOT_ELIGIBLE"),
        )
        assertEquals(
            AttendanceReportFailure.SESSION_NOT_FOUND,
            AttendanceReportFailure.fromRawCode("ATTENDANCE_SESSION_NOT_FOUND"),
        )
        assertEquals(
            AttendanceReportFailure.UNAVAILABLE,
            AttendanceReportFailure.fromRawCode("ATTENDANCE_REPORT_UNAVAILABLE"),
        )
        assertEquals(
            AttendanceReportFailure.SESSION_CHANGED,
            AttendanceReportFailure.fromRawCode("ATTENDANCE_REPORT_SESSION_CHANGED"),
        )
        assertEquals(
            AttendanceReportFailure.SESSION_CHANGED,
            AttendanceReportFailure.fromRawCode("ATTENDANCE_REPORT_SESSION_CONFLICT"),
        )
        assertEquals(
            AttendanceReportFailure.INVALID_REPORT,
            AttendanceReportFailure.fromRawCode("INVALID_ATTENDANCE_REPORT"),
        )
        assertEquals(
            AttendanceReportFailure.UNKNOWN,
            AttendanceReportFailure.fromRawCode("SOME_UNKNOWN_CODE"),
        )
        assertNull(AttendanceReportFailure.fromRawCode(null))
    }
}
