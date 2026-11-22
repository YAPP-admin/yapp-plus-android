package com.yapp.plus.domain.session

data class SessionCoordinates(
    val latitude: Double,
    val longitude: Double,
) {
    val isValid: Boolean
        get() = latitude in -90.0..90.0 && longitude in -180.0..180.0
}
