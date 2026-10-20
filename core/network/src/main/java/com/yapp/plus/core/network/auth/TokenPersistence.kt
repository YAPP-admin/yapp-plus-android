package com.yapp.plus.core.network.auth

interface TokenPersistence {
    suspend fun fetchTokens(): Tokens?

    suspend fun updateTokens(tokens: Tokens?)
}
