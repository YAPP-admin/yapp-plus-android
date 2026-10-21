package com.yapp.plus.data.supabase

import com.yapp.plus.data.auth.AuthDataSource
import com.yapp.plus.data.auth.AuthTokenLocalDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class SupabaseRestClient(
    private val httpClient: HttpClient,
    private val projectUrl: String,
    private val publishableKey: String,
    private val tokenLocalDataSource: AuthTokenLocalDataSource,
    private val authDataSource: AuthDataSource,
) {
    suspend fun <T> fetch(
        path: String,
        parameters: List<Pair<String, String>> = emptyList(),
        transform: (String) -> T,
    ): T = execute(path, HttpMethod.Get, parameters, body = null, transform)

    suspend fun <T> post(
        path: String,
        body: JsonObject,
        transform: (String) -> T,
    ): T = execute(path, HttpMethod.Post, emptyList(), body, transform)

    suspend fun <T> upsert(
        path: String,
        conflictColumns: String,
        body: JsonObject,
        transform: (String) -> T,
    ): T =
        execute(
            path = path,
            method = HttpMethod.Post,
            parameters = listOf("on_conflict" to conflictColumns),
            body = body,
            transform = transform,
            prefer = "resolution=merge-duplicates,return=minimal",
        )

    private suspend fun <T> execute(
        path: String,
        method: HttpMethod,
        parameters: List<Pair<String, String>>,
        body: JsonObject?,
        transform: (String) -> T,
        prefer: String? = null,
    ): T =
        try {
            val response = authenticatedRequest(path, method, parameters, body, prefer)
            if (response.status.value !in 200..299) {
                throw requestFailure(response, method)
            }
            transform(response.bodyAsText())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SupabaseDataSourceException) {
            currentCoroutineContext().ensureActive()
            throw exception
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
            // Transport, JSON and mapper failures may contain credentials or response contents.
            throw SupabaseDataSourceException("Data operation failed")
        }

    private suspend fun authenticatedRequest(
        path: String,
        method: HttpMethod,
        parameters: List<Pair<String, String>>,
        body: JsonObject?,
        prefer: String?,
    ): HttpResponse {
        val response = request(path, method, parameters, body, fetchAccessToken(), prefer)
        if (response.status != HttpStatusCode.Unauthorized) return response
        if (authDataSource.fetchSession() == null) {
            throw SupabaseDataSourceException("Authentication required")
        }
        return request(path, method, parameters, body, fetchAccessToken(), prefer)
    }

    private suspend fun fetchAccessToken(): String =
        tokenLocalDataSource.fetchTokens().accessToken?.takeIf { it.isNotBlank() }
            ?: throw SupabaseDataSourceException("Authentication required")

    private suspend fun request(
        path: String,
        method: HttpMethod,
        parameters: List<Pair<String, String>>,
        body: JsonObject?,
        accessToken: String,
        prefer: String?,
    ): HttpResponse {
        if (!publishableKey.startsWith("sb_publishable_")) {
            throw SupabaseDataSourceException("Configure supabase.publishableKey for data requests")
        }
        return httpClient.request("${projectUrl.trimEnd('/')}/rest/v1/$path") {
            this.method = method
            expectSuccess = false
            header("apikey", publishableKey)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            if (prefer != null) header("Prefer", prefer)
            for ((name, value) in parameters) url.parameters.append(name, value)
            if (body != null) {
                contentType(ContentType.Application.Json)
                setBody(body.toString())
            }
        }
    }

    private suspend fun requestFailure(
        response: HttpResponse,
        method: HttpMethod,
    ): SupabaseDataSourceException {
        val code =
            if (method == HttpMethod.Post) readErrorCode(response.bodyAsText()) else null
        return SupabaseDataSourceException(
            message = "Data request failed (${response.status.value})",
            code = code,
        )
    }

    private fun readErrorCode(body: String): String? {
        val error = runCatching { Json.decodeFromString<JsonObject>(body) }.getOrNull()
        return INVALID_ATTENDANCE_CODE.takeIf { marker ->
            listOf("code", "message").any {
                (error?.get(it) as? JsonPrimitive)?.content == marker
            }
        }
    }

    private companion object {
        const val INVALID_ATTENDANCE_CODE = "INVALID_ATTENDANCE_CODE"
    }
}
