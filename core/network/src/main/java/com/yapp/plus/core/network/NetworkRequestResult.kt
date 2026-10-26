package com.yapp.plus.core.network

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface NetworkRequestResult<out T> {
    data class Success<T>(
        val data: T,
    ) : NetworkRequestResult<T>

    data class Error(
        val error: NetworkRequestError,
    ) : NetworkRequestResult<Nothing>
}

fun <T> NetworkRequestResult<T>.getOrThrow(): T =
    when (this) {
        is NetworkRequestResult.Success -> data
        is NetworkRequestResult.Error -> throw error.cause
    }

suspend fun <T> invokeRequest(call: suspend () -> T): NetworkRequestResult<T> =
    try {
        NetworkRequestResult.Success(call())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: ResponseException) {
        val statusCode = exception.response.status.value
        val safeException =
            ResponseException(
                exception.response,
                "HTTP request failed with status=$statusCode",
            )
        logRequestFailure(
            "request http failure status=$statusCode, " +
                "type=${exception.javaClass.simpleName}",
        )
        NetworkRequestResult.Error(NetworkRequestError.Http(statusCode, safeException))
    } catch (exception: HttpRequestTimeoutException) {
        logRequestFailure("request timeout type=${exception.javaClass.simpleName}")
        NetworkRequestResult.Error(NetworkRequestError.Timeout(exception))
    } catch (exception: IOException) {
        logRequestFailure("request network failure type=${exception.javaClass.simpleName}")
        NetworkRequestResult.Error(NetworkRequestError.Network(exception))
    } catch (exception: SerializationException) {
        logRequestFailure(
            "request serialization failure type=${exception.javaClass.simpleName}",
        )
        NetworkRequestResult.Error(NetworkRequestError.Serialization(exception))
    } catch (exception: Exception) {
        logRequestFailure("request unknown failure type=${exception.javaClass.simpleName}")
        NetworkRequestResult.Error(NetworkRequestError.Unknown(exception))
    }

private fun logRequestFailure(message: String) {
    try {
        AndroidNetworkLogger.log(message)
    } catch (_: RuntimeException) {
        // Logging must not replace the network result in local JVM tests.
    }
}
