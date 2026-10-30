package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamPhotoPresignResponse(
    val imageKey: String,
    val uploadUrl: String,
    val signedHeaders: Map<String, String>,
    val expiresAt: String,
)
