package com.yapp.plus.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.net.URI

object ApiHttpClientFactory {
    fun create(accessTokenProvider: AccessTokenProvider): HttpClient {
        val apiOrigin = URI.create(BuildConfig.API_BASE_URL)

        return HttpClient(Android) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(DefaultRequest) {
                val shouldAttachAuthorization = shouldAttachAuthorization(url, apiOrigin)
                url(BuildConfig.API_BASE_URL)
                if (shouldAttachAuthorization) {
                    accessTokenProvider.currentAccessToken()?.let { accessToken ->
                        header(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
            }
        }
    }

    internal fun shouldAttachAuthorization(
        requestUrl: URLBuilder,
        apiOrigin: URI,
    ): Boolean = requestUrl.host.isBlank() || isApiOrigin(requestUrl, apiOrigin)

    private fun isApiOrigin(
        requestUrl: URLBuilder,
        apiOrigin: URI,
    ): Boolean {
        val expectedPort = apiOrigin.port.takeIf { it >= 0 } ?: defaultPort(apiOrigin.scheme)

        return requestUrl.protocol.name == apiOrigin.scheme &&
            requestUrl.host.equals(apiOrigin.host, ignoreCase = true) &&
            requestUrl.port == expectedPort
    }

    private fun defaultPort(scheme: String): Int =
        when (scheme.lowercase()) {
            "https" -> 443
            "http" -> 80
            else -> -1
        }
}
