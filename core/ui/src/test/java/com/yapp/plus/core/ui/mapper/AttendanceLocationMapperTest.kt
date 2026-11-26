package com.yapp.plus.core.ui.mapper

import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.domain.attendance.AttendanceLocationFailure
import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceLocationMapperTest {
    @Test
    fun mapsAttendanceLocationFailureToHomeAttendanceLocationIssueCorrectly() {
        assertEquals(
            HomeAttendanceLocationIssue.PrecisePermissionRequired,
            AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED.toUiIssue(),
        )
        assertEquals(
            HomeAttendanceLocationIssue.LocationDisabled,
            AttendanceLocationFailure.LOCATION_DISABLED.toUiIssue(),
        )
        assertEquals(
            HomeAttendanceLocationIssue.TimedOut,
            AttendanceLocationFailure.TIMED_OUT.toUiIssue(),
        )
        assertEquals(
            HomeAttendanceLocationIssue.Unavailable,
            AttendanceLocationFailure.UNAVAILABLE.toUiIssue(),
        )
        assertEquals(
            HomeAttendanceLocationIssue.InsufficientAccuracy,
            AttendanceLocationFailure.INSUFFICIENT_ACCURACY.toUiIssue(),
        )
        assertEquals(
            HomeAttendanceLocationIssue.OutOfRange,
            AttendanceLocationFailure.OUT_OF_RANGE.toUiIssue(),
        )
    }
}
