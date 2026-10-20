package com.yapp.plus.core.network.auth

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class TokenStore(
    initialTokens: Tokens? = null,
    private val persistence: TokenPersistence? = null,
) {
    private val mutex = Mutex()
    private var cachedTokens: Tokens? = initialTokens
    private var isInitialized = initialTokens != null

    suspend fun currentTokens(): Tokens? =
        mutex.withLock {
            ensureInitialized()
            cachedTokens
        }

    suspend fun updateTokens(tokens: Tokens?) {
        mutex.withLock { persistTokens(tokens) }
    }

    suspend fun replaceTokens(
        expected: Tokens,
        updated: Tokens?,
    ): Boolean =
        mutex.withLock {
            ensureInitialized()
            if (cachedTokens !== expected) return false
            persistTokens(updated)
            true
        }

    private suspend fun ensureInitialized() {
        if (isInitialized) return
        cachedTokens = persistence?.fetchTokens()
        isInitialized = true
    }

    private suspend fun persistTokens(updated: Tokens?) {
        withContext(NonCancellable) {
            persistence?.updateTokens(updated)
            cachedTokens = updated
            isInitialized = true
        }
    }
}
