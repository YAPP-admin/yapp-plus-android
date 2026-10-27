package com.yapp.plus.core.network

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface NetworkRequestError {
    data class Http(
        val statusCode: Int,
        val cause: ResponseException,
    ) : NetworkRequestError

    data class Timeout(
        val cause: HttpRequestTimeoutException,
    ) : NetworkRequestError

    data class Network(
        val cause: IOException,
    ) : NetworkRequestError

    data class Serialization(
        val cause: SerializationException,
    ) : NetworkRequestError

    data class Unknown(
        val cause: Exception,
    ) : NetworkRequestError
}
