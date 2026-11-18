package com.yapp.plus.domain.session

data class SessionLocationDetails(
    val roadAddress: String?,
    val detailAddress: String?,
    val coordinates: SessionCoordinates?,
) {
    val addressLine: String?
        get() = roadAddress?.let { listOfNotNull(it, detailAddress).joinToString(" ") }
}
