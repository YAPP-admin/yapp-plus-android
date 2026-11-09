package com.yapp.plus.data.profile

import com.yapp.plus.domain.profile.MembershipStatus
import com.yapp.plus.domain.profile.UserMembership
import com.yapp.plus.domain.profile.UserPosition
import com.yapp.plus.domain.profile.UserProfile

data class UserProfileData(
    val userId: Long,
    val name: String,
    val membershipStatus: String?,
    val generationId: Long?,
    val generationNumber: Int?,
    val position: String?,
) {
    fun toDomain(): UserProfile = UserProfile(userId, name, toMembership())

    private fun toMembership(): UserMembership? {
        if (membershipStatus == null && generationId == null && generationNumber == null && position == null) {
            return null
        }
        val status = checkNotNull(membershipStatus)
        return UserMembership(
            status =
                MembershipStatus.entries.firstOrNull { it.name == status }
                    ?: MembershipStatus.UNKNOWN,
            generationId = checkNotNull(generationId),
            generationNumber = checkNotNull(generationNumber),
            position =
                position?.let { value ->
                    UserPosition.entries.firstOrNull { it.name == value } ?: UserPosition.UNKNOWN
                },
        )
    }
}
