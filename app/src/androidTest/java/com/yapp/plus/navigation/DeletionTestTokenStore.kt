package com.yapp.plus.navigation

import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.data.auth.AuthTokenLocalDataSource

class DeletionTestTokenStore : AuthTokenLocalDataSource {
    @Volatile
    var tokens = SessionTokenRecord(accessToken = "fake-access", refreshToken = "fake-refresh")

    override suspend fun fetchTokens(): SessionTokenRecord = tokens

    override suspend fun updateTokens(tokens: SessionTokenRecord) {
        this.tokens = tokens
    }
}
