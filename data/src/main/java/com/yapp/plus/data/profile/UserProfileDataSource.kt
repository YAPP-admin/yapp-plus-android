package com.yapp.plus.data.profile

fun interface UserProfileDataSource {
    suspend fun fetchProfile(): UserProfileData
}
