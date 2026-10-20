package com.yapp.plus.core.network.auth

import kotlinx.coroutines.CompletableDeferred
import java.io.IOException

internal class FakeTokenPersistence(
    var storedTokens: Tokens? = null,
) : TokenPersistence {
    var readCount = 0
    var writeCount = 0
    var readFailure: IOException? = null
    var writeFailure: IOException? = null
    var readGate: CompletableDeferred<Unit>? = null
    var writeGate: CompletableDeferred<Unit>? = null
    val readStarted = CompletableDeferred<Unit>()
    val writeStarted = CompletableDeferred<Unit>()

    override suspend fun fetchTokens(): Tokens? {
        readCount++
        readStarted.complete(Unit)
        readGate?.await()
        readFailure?.let { throw it }
        return storedTokens?.let {
            Tokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
        }
    }

    override suspend fun updateTokens(tokens: Tokens?) {
        writeCount++
        writeStarted.complete(Unit)
        writeGate?.await()
        writeFailure?.let { throw it }
        storedTokens = tokens
    }
}
