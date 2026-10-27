package com.yapp.plus.core.network.auth

@ConsistentCopyVisibility
data class Tokens private constructor(
    val accessToken: String,
    val refreshToken: String?,
    internal val sessionId: Any,
) {
    constructor(
        accessToken: String,
        refreshToken: String?,
    ) : this(accessToken, refreshToken, Any())

    internal fun renewedWith(tokens: Tokens): Tokens =
        Tokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            sessionId = sessionId,
        )
}
