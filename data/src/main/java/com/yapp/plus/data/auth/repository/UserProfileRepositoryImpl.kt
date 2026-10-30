package com.yapp.plus.data.auth.repository

import com.yapp.plus.data.auth.datasource.session.SessionProfileDataSource
import com.yapp.plus.domain.auth.UserProfileRepository

class UserProfileRepositoryImpl(
    private val source: SessionProfileDataSource,
) : UserProfileRepository {
    override suspend fun fetchName(): String? = source.fetchName()
}
