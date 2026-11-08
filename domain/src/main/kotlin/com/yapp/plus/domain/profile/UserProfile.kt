package com.yapp.plus.domain.profile

data class UserProfile(
    val userId: Long,
    val name: String,
    val membership: UserMembership?,
)
