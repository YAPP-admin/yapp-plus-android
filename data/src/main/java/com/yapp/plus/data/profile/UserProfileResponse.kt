package com.yapp.plus.data.profile

import kotlinx.serialization.Serializable

@Serializable
internal data class UserProfileResponse(
    val userId: Long,
    val name: String,
    val membershipStatus: String?,
    val generationId: Long?,
    val generationNumber: Int?,
    val position: String?,
) {
    fun toData(): UserProfileData =
        UserProfileData(
            userId = userId,
            name = name,
            membershipStatus = membershipStatus,
            generationId = generationId,
            generationNumber = generationNumber,
            position = position,
        )
}
