package com.yapp.plus.core.network

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface NetworkRequestError {
    val cause: Exception

    data class Http(
        val statusCode: Int,
        override val cause: ResponseException,
    ) : NetworkRequestError

    data class Timeout(
        override val cause: HttpRequestTimeoutException,
    ) : NetworkRequestError

    data class Network(
        override val cause: IOException,
    ) : NetworkRequestError

    data class Serialization(
        override val cause: SerializationException,
    ) : NetworkRequestError

    data class Unknown(
        override val cause: Exception,
    ) : NetworkRequestError
}
