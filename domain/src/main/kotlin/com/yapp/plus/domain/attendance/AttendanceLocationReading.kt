package com.yapp.plus.domain.attendance

sealed interface AttendanceLocationReading {
    data class Measured(
        val meters: Float,
        val accuracyMeters: Float?,
    ) : AttendanceLocationReading

    data class Failed(
        val reason: AttendanceLocationFailure,
    ) : AttendanceLocationReading
}
