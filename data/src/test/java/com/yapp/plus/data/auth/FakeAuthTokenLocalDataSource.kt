package com.yapp.plus.data.auth

import com.yapp.plus.core.datastore.SessionTokenRecord

class FakeAuthTokenLocalDataSource(
    var tokens: SessionTokenRecord = SessionTokenRecord(),
) : AuthTokenLocalDataSource {
    override suspend fun fetchTokens(): SessionTokenRecord = tokens

    override suspend fun updateTokens(tokens: SessionTokenRecord) {
        this.tokens = tokens
    }
}
