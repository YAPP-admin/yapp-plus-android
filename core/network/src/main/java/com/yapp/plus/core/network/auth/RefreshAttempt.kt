package com.yapp.plus.core.network.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

internal class RefreshAttempt(
    val tokens: Tokens,
) {
    private var task: Deferred<Result<Tokens?>>? = null

    val isCompleted: Boolean
        get() = task?.isCompleted == true

    suspend fun await(
        scope: CoroutineScope,
        renew: suspend () -> Tokens?,
    ): Tokens? {
        val task =
            synchronized(this) {
                task ?: scope.async { renewalResult(renew) }.also { task = it }
            }
        return task.await().getOrThrow()
    }

    private suspend fun renewalResult(renew: suspend () -> Tokens?): Result<Tokens?> =
        runCatching {
            renew()
        }
}
