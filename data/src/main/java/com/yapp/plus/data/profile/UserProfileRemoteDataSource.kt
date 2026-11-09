package com.yapp.plus.data.profile

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UserProfileRemoteDataSource(
    private val client: HttpClient,
) : UserProfileDataSource {
    override suspend fun fetchProfile(): UserProfileData = client.get("users/me").body<UserProfileResponse>().toData()
}
