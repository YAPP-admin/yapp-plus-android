package com.yapp.plus.data.supabase

import com.yapp.plus.data.auth.AuthDataSource
import com.yapp.plus.data.auth.AuthTokenLocalDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

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
    ): T =
        try {
            val response = authenticatedRequest(path, parameters)
            if (response.status.value !in 200..299) {
                throw SupabaseDataSourceException("Data request failed (${response.status.value})")
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
        parameters: List<Pair<String, String>>,
    ): HttpResponse {
        val response = request(path, parameters, fetchAccessToken())
        if (response.status != HttpStatusCode.Unauthorized) return response
        if (authDataSource.fetchSession() == null) {
            throw SupabaseDataSourceException("Authentication required")
        }
        return request(path, parameters, fetchAccessToken())
    }

    private suspend fun fetchAccessToken(): String =
        tokenLocalDataSource.fetchTokens().accessToken?.takeIf { it.isNotBlank() }
            ?: throw SupabaseDataSourceException("Authentication required")

    private suspend fun request(
        path: String,
        parameters: List<Pair<String, String>>,
        accessToken: String,
    ): HttpResponse {
        if (!publishableKey.startsWith("sb_publishable_")) {
            throw SupabaseDataSourceException("Configure supabase.publishableKey for data requests")
        }
        return httpClient.get("${projectUrl.trimEnd('/')}/rest/v1/$path") {
            expectSuccess = false
            header("apikey", publishableKey)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            for ((name, value) in parameters) url.parameters.append(name, value)
        }
    }
}
