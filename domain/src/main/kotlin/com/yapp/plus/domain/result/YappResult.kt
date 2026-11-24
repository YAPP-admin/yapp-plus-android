package com.yapp.plus.domain.result

sealed interface YappResult<out T, out E : Any> {
    data class Success<out T>(
        val value: T,
    ) : YappResult<T, Nothing>

    data class Failure<out E : Any>(
        val reason: E,
    ) : YappResult<Nothing, E>
}
