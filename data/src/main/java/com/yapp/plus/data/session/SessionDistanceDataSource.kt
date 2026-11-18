package com.yapp.plus.data.session

import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance

fun interface SessionDistanceDataSource {
    suspend fun fetchDistanceTo(coordinates: SessionCoordinates): SessionDistance
}
