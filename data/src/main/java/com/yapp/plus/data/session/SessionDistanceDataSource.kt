package com.yapp.plus.data.session

import com.yapp.plus.domain.attendance.AttendanceLocationReading
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance

interface SessionDistanceDataSource {
    suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance

    suspend fun fetchPreciseDistanceTo(coordinates: SessionCoordinates): AttendanceLocationReading
}
