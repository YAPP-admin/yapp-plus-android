package com.yapp.plus.domain.profile

fun interface UserProfileReadRepository {
    suspend fun fetchProfile(): UserProfile
}
