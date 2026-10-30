package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable

@Serializable
internal data class TeamPhotoSubmitRequest(
    val imageKey: String,
    val selectedMembershipIds: Set<Long>,
)
