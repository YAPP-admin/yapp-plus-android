package com.yapp.plus.data.afterparty

import kotlinx.serialization.Serializable

@Serializable
internal data class SettlementPhotoReservation(
    val imageId: Long,
    val uploadUrl: String,
    val signedHeaders: Map<String, String>,
    val expiresAt: String,
)
