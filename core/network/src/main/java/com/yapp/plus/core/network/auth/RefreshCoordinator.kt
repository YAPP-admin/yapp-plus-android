package com.yapp.plus.core.network.auth

import kotlinx.coroutines.CoroutineScope

internal class RefreshCoordinator(
    private val tokenStore: TokenStore,
    private val tokenRefresher: TokenRefresher,
    private val scope: CoroutineScope,
) {
    private var attempt: RefreshAttempt? = null

    fun prepare(tokens: Tokens): RefreshAttempt? {
        if (tokens.refreshToken == null) return null
        return synchronized(this) {
            attempt?.takeIf { it.tokens === tokens && !it.isCompleted }
                ?: RefreshAttempt(tokens).also { attempt = it }
        }
    }

    suspend fun refresh(attempt: RefreshAttempt): Tokens? {
        attempt.await(scope) { renew(attempt.tokens) } ?: return null
        return tokenStore.currentTokens()?.takeIf { it.sessionId === attempt.tokens.sessionId }
    }

    private suspend fun renew(expired: Tokens): Tokens? {
        val current = tokenStore.currentTokens()
        if (current !== expired) return current?.takeIf { it.sessionId === expired.sessionId }
        val refreshToken = expired.refreshToken ?: return null
        val updated =
            when (val result = tokenRefresher.refresh(refreshToken)) {
                is RefreshResult.Success -> expired.renewedWith(result.tokens)
                RefreshResult.Rejected -> null
            }
        if (tokenStore.replaceTokens(expired, updated)) return updated
        return tokenStore.currentTokens()?.takeIf { it.sessionId === expired.sessionId }
    }
}
