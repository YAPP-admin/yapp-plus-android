package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamPhotoSubmitResponse(
    val photoId: Long,
    val submittedAt: String,
    val status: String,
)
