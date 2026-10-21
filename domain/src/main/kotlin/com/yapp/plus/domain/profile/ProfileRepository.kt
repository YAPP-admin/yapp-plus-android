package com.yapp.plus.domain.profile

interface ProfileRepository {
    suspend fun fetchProfile(): Profile?

    suspend fun saveProfile(profile: Profile)
}
