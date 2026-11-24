package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamPhotoLookupResponse(
    val photo: TeamPhotoDetailsResponse?,
)
