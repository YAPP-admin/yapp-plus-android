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
        AndroidNetworkLogger.log(
            "request http failure status=${exception.response.status.value}, " +
                "type=${exception.javaClass.simpleName}",
        )
        NetworkRequestResult.Error(
            NetworkRequestError.Http(exception.response.status.value, exception),
        )
    } catch (exception: HttpRequestTimeoutException) {
        AndroidNetworkLogger.log("request timeout type=${exception.javaClass.simpleName}")
        NetworkRequestResult.Error(NetworkRequestError.Timeout(exception))
    } catch (exception: IOException) {
        AndroidNetworkLogger.log("request network failure type=${exception.javaClass.simpleName}")
        NetworkRequestResult.Error(NetworkRequestError.Network(exception))
    } catch (exception: SerializationException) {
        AndroidNetworkLogger.log(
            "request serialization failure type=${exception.javaClass.simpleName}",
        )
        NetworkRequestResult.Error(NetworkRequestError.Serialization(exception))
    } catch (exception: Exception) {
        AndroidNetworkLogger.log("request unknown failure type=${exception.javaClass.simpleName}")
        NetworkRequestResult.Error(NetworkRequestError.Unknown(exception))
    }
