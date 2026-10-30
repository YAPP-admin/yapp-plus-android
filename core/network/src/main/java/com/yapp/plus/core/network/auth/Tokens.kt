package com.yapp.plus.core.network.auth

import java.util.UUID

@ConsistentCopyVisibility
data class Tokens private constructor(
    val accessToken: String,
    val refreshToken: String?,
    internal val sessionId: Any,
    val storageKey: String,
) {
    constructor(
        accessToken: String,
        refreshToken: String?,
        storageKey: String = UUID.randomUUID().toString(),
    ) : this(accessToken, refreshToken, Any(), storageKey)

    internal fun renewedWith(tokens: Tokens): Tokens =
        Tokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            sessionId = sessionId,
            storageKey = storageKey,
        )
}
