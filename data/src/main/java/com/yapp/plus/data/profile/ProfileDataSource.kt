package com.yapp.plus.data.profile

interface ProfileDataSource {
    suspend fun fetchProfile(): ProfileData?

    suspend fun saveProfile(profile: ProfileData)
}
