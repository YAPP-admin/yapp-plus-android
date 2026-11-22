package com.yapp.plus.domain.attendance

enum class AttendanceLocationFailure {
    PRECISE_PERMISSION_REQUIRED,
    LOCATION_DISABLED,
    TIMED_OUT,
    UNAVAILABLE,
    INSUFFICIENT_ACCURACY,
    OUT_OF_RANGE,
}
