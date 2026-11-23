package com.yapp.plus.data.session

import com.yapp.plus.domain.attendance.AttendanceLocationReading
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.domain.session.SessionDistanceRepository

class SessionDistanceRepositoryImpl(
    private val dataSource: SessionDistanceDataSource,
) : SessionDistanceRepository {
    override suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance =
        dataSource.fetchDistanceTo(coordinates)

    override suspend fun fetchPreciseDistanceTo(coordinates: SessionCoordinates): AttendanceLocationReading =
        dataSource.fetchPreciseDistanceTo(coordinates)
}
