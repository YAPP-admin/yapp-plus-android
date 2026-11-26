package com.yapp.plus.core.ui.mapper

import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.domain.attendance.AttendanceLocationFailure

fun AttendanceLocationFailure.toUiIssue(): HomeAttendanceLocationIssue =
    when (this) {
        AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED ->
            HomeAttendanceLocationIssue.PrecisePermissionRequired
        AttendanceLocationFailure.LOCATION_DISABLED -> HomeAttendanceLocationIssue.LocationDisabled
        AttendanceLocationFailure.TIMED_OUT -> HomeAttendanceLocationIssue.TimedOut
        AttendanceLocationFailure.UNAVAILABLE -> HomeAttendanceLocationIssue.Unavailable
        AttendanceLocationFailure.INSUFFICIENT_ACCURACY ->
            HomeAttendanceLocationIssue.InsufficientAccuracy
        AttendanceLocationFailure.OUT_OF_RANGE -> HomeAttendanceLocationIssue.OutOfRange
    }
