package com.yapp.plus.domain.profile

data class UserMembership(
    val status: MembershipStatus,
    val generationId: Long,
    val generationNumber: Int,
    val position: UserPosition?,
)
