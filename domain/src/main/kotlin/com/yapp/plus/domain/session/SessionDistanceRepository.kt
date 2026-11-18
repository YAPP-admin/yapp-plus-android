package com.yapp.plus.domain.session

fun interface SessionDistanceRepository {
    suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance
}
