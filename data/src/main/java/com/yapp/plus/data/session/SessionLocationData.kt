package com.yapp.plus.data.session

import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionLocationDetails

data class SessionLocationData(
    val roadAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val detailAddress: String?,
) {
    internal fun takeIfPresent(): SessionLocationData? =
        takeUnless {
            roadAddress == null && latitude == null && longitude == null && detailAddress == null
        }

    internal fun toDomain(): SessionLocationDetails? {
        val road = roadAddress?.trim()?.takeIf(String::isNotEmpty)
        val detail = detailAddress?.trim()?.takeIf(String::isNotEmpty)
        val coordinates = coordinatesOrNull()
        if (road == null && detail == null && coordinates == null) return null
        return SessionLocationDetails(
            roadAddress = road,
            detailAddress = detail,
            coordinates = coordinates,
        )
    }

    private fun coordinatesOrNull(): SessionCoordinates? {
        if (latitude == null || longitude == null) return null
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
        return SessionCoordinates(latitude = latitude, longitude = longitude)
    }
}
