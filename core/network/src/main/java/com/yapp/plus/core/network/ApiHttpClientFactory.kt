package com.yapp.plus.core.network

import com.yapp.plus.core.network.auth.RefreshCoordinator
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.Json
import java.net.URI

object ApiHttpClientFactory {
    fun create(
        tokenStore: TokenStore,
        tokenRefresher: TokenRefresher,
    ): HttpClient =
        create(
            Android.create(),
            BuildConfig.API_BASE_URL,
            tokenStore,
            tokenRefresher,
            isLoggingEnabled = BuildConfig.DEBUG,
        )

    fun createUnauthenticated(): HttpClient =
        createBaseClient(Android.create(), BuildConfig.API_BASE_URL, BuildConfig.DEBUG)

    internal fun create(
        engine: HttpClientEngine,
        baseUrl: String,
        tokenStore: TokenStore,
        tokenRefresher: TokenRefresher,
        isLoggingEnabled: Boolean = false,
        renewalScope: CoroutineScope? = null,
    ): HttpClient {
        val client = createBaseClient(engine, baseUrl, isLoggingEnabled)
        val scope = setupRenewalScope(client, renewalScope)
        val coordinator = RefreshCoordinator(tokenStore, tokenRefresher, scope)
        val apiOrigin = URI.create(baseUrl)
        installAuthInterceptor(client, tokenStore, coordinator, apiOrigin)
        return client
    }

    private fun setupRenewalScope(
        client: HttpClient,
        renewalScope: CoroutineScope?,
    ): CoroutineScope {
        val scope = renewalScope ?: CoroutineScope(SupervisorJob() + Dispatchers.IO)
        if (renewalScope == null) {
            client.coroutineContext[Job]?.invokeOnCompletion { scope.cancel() }
        }
        return scope
    }

    private fun installAuthInterceptor(
        client: HttpClient,
        tokenStore: TokenStore,
        coordinator: RefreshCoordinator,
        apiOrigin: URI,
    ) {
        client.plugin(HttpSend).intercept { request ->
            executeAuthenticatedRequest(
                request = request,
                execute = { execute(it) },
                tokenStore = tokenStore,
                coordinator = coordinator,
                apiOrigin = apiOrigin,
            )
        }
    }

    private suspend fun executeAuthenticatedRequest(
        request: HttpRequestBuilder,
        execute: suspend (HttpRequestBuilder) -> HttpClientCall,
        tokenStore: TokenStore,
        coordinator: RefreshCoordinator,
        apiOrigin: URI,
    ): HttpClientCall {
        if (!shouldAttachAuthorization(request.url, apiOrigin)) {
            return execute(request)
        }
        val tokens = tokenStore.currentTokens() ?: return execute(request)
        val attempt = coordinator.prepare(tokens)
        request.headers[HttpHeaders.Authorization] = "Bearer ${tokens.accessToken}"
        val call = execute(request)
        if (call.response.status != HttpStatusCode.Unauthorized || attempt == null) {
            return call
        }
        val renewed = coordinator.refresh(attempt) ?: return call
        request.headers[HttpHeaders.Authorization] = "Bearer ${renewed.accessToken}"
        return execute(request)
    }

    private fun createBaseClient(
        engine: HttpClientEngine,
        baseUrl: String,
        isLoggingEnabled: Boolean,
    ): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            installLogging(isLoggingEnabled)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(DefaultRequest) { url(baseUrl) }
        }

    private fun HttpClientConfig<*>.installLogging(isEnabled: Boolean) {
        if (!isEnabled) return
        install(Logging) {
            logger = AndroidNetworkLogger
            level = LogLevel.HEADERS
            sanitizeHeader { header -> isSensitiveHeader(header) }
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

    private fun isSensitiveHeader(header: String): Boolean =
        sensitiveHeaderMarkers.any { marker -> header.contains(marker, ignoreCase = true) }

    private val sensitiveHeaderMarkers =
        setOf("authorization", "cookie", "token", "api-key", "secret")
}
