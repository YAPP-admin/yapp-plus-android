package com.yapp.plus.domain.attendance

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class AttendanceReportPolicyTest {
    @Test
    fun requiresNonBlankReasonWithinServerLengthLimit() {
        assertFalse(AttendanceReportPolicy.isReasonValid(""))
        assertFalse(AttendanceReportPolicy.isReasonValid(" \t\n\u3000"))
        assertTrue(AttendanceReportPolicy.isReasonValid(" 개인 일정 "))
        assertTrue(AttendanceReportPolicy.isReasonValid("가".repeat(30)))
        assertTrue(AttendanceReportPolicy.isReasonValid("가".repeat(28) + "😀"))
        assertFalse(AttendanceReportPolicy.isReasonValid("가".repeat(29) + "😀"))
    }

    @Test
    fun closesExactlyAtSeoulSessionStartAndRejectsMissingSchedule() {
        val date = LocalDate.of(2026, 11, 21)
        val time = LocalTime.of(14, 0)
        val start = Instant.parse("2026-11-21T05:00:00Z")
        assertTrue(AttendanceReportPolicy.canSubmit(date, time, start.minusNanos(1)))
        assertFalse(AttendanceReportPolicy.canSubmit(date, time, start))
        assertFalse(AttendanceReportPolicy.canSubmit(date, time, start.plusSeconds(1)))
        assertFalse(AttendanceReportPolicy.canSubmit(null, time, start))
        assertFalse(AttendanceReportPolicy.canSubmit(date, null, start))
    }
}
