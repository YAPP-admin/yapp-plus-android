package com.yapp.plus.data.auth

import com.yapp.plus.core.datastore.SessionTokenRecord

interface AuthTokenLocalDataSource {
    suspend fun fetchTokens(): SessionTokenRecord

    suspend fun updateTokens(tokens: SessionTokenRecord)
}
