package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamPhotoPresignRequest(
    val filename: String,
    val contentType: String,
    val sizeBytes: Long,
    val checksumSha256: String,
)
