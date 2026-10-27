package com.yapp.plus.data.auth

import com.yapp.plus.core.network.auth.TokenPersistence
import com.yapp.plus.core.network.auth.Tokens

internal class FakeAuthTokenPersistence(
    var tokens: Tokens?,
) : TokenPersistence {
    var updateFailure: Exception? = null
    var updateCount: Int = 0
        private set

    override suspend fun fetchTokens(): Tokens? = tokens

    override suspend fun updateTokens(tokens: Tokens?) {
        updateCount++
        updateFailure?.let { throw it }
        this.tokens = tokens
    }
}
