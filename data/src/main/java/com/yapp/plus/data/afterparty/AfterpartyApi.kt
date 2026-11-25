package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.result.YappResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.ParametersBuilder
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI

internal suspend inline fun <reified T> HttpClient.afterpartyGet(
    path: String,
    crossinline query: ParametersBuilder.() -> Unit = {},
): T = get(path) { url { parameters.query() } }.body()

internal suspend inline fun <reified T> HttpClient.afterpartyPut(
    path: String,
    request: JsonObject,
): T =
    put(path) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()

internal suspend inline fun <reified T> HttpClient.afterpartyPost(
    path: String,
    request: JsonObject,
): T =
    post(path) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()

internal suspend inline fun <T> afterpartyRequest(block: () -> T): AfterpartyResult<T> =
    try {
        YappResult.Success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (exception: ResponseException) {
        YappResult.Failure(exception.afterpartyFailure())
    } catch (_: Exception) {
        YappResult.Failure(AfterpartyFailure.Unavailable)
    }

internal suspend fun ResponseException.afterpartyFailure(): AfterpartyFailure {
    val body = response.bodyAsText()
    val code =
        runCatching {
            Json
                .parseToJsonElement(body)
                .jsonObject["code"]
                ?.jsonPrimitive
                ?.content
        }.getOrNull()
    return when (code) {
        "INVALID_AFTERPARTY_REQUEST", "INVALID_SETTLEMENT_REQUEST" -> AfterpartyFailure.InvalidRequest
        "AFTERPARTY_RESPONSE_REQUIRED" -> AfterpartyFailure.ResponseRequired
        "AFTERPARTY_VOTING_CLOSED" -> AfterpartyFailure.VotingClosed
        "SETTLEMENT_REVISION_CONFLICT" -> AfterpartyFailure.RevisionConflict
        "SETTLEMENT_FINANCIAL_EDIT_LOCKED" -> AfterpartyFailure.FinancialEditLocked
        else ->
            when (response.status.value) {
                401 -> AfterpartyFailure.AuthenticationRequired
                403 -> AfterpartyFailure.Forbidden
                404 -> AfterpartyFailure.NotFound
                else -> AfterpartyFailure.Unavailable
            }
    }
}

internal fun isSecurePhotoUrl(value: String): Boolean =
    runCatching {
        val uri = URI.create(value)
        uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.userInfo == null && uri.fragment == null
    }.getOrDefault(false)
