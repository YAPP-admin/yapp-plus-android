package com.yapp.plus.domain.session

import com.yapp.plus.domain.attendance.AttendanceLocationFailure
import com.yapp.plus.domain.attendance.AttendanceLocationReading

fun interface SessionDistanceRepository {
    suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance

    suspend fun fetchPreciseDistanceTo(coordinates: SessionCoordinates): AttendanceLocationReading =
        AttendanceLocationReading.Failed(AttendanceLocationFailure.UNAVAILABLE)
}
