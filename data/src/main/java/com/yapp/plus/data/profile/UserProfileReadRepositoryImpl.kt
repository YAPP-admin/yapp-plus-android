package com.yapp.plus.data.profile

import com.yapp.plus.domain.profile.UserProfile
import com.yapp.plus.domain.profile.UserProfileReadRepository

class UserProfileReadRepositoryImpl(
    private val source: UserProfileDataSource,
) : UserProfileReadRepository {
    override suspend fun fetchProfile(): UserProfile = source.fetchProfile().toDomain()
}
