package com.yapp.plus.data.auth

import com.yapp.plus.core.datastore.SessionTokenRecord
import com.yapp.plus.domain.auth.GoogleLoginCredential
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthDataSource(
    private val httpClient: HttpClient,
    private val projectUrl: String,
    private val publishableKey: String,
    private val tokenLocalDataSource: AuthTokenLocalDataSource,
) : AuthDataSource {
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun signInWithGoogle(credential: GoogleLoginCredential): AuthSessionData =
        serialized {
            if (credential.idToken.isBlank() || credential.rawNonce.isBlank()) {
                throw AuthDataSourceException("Missing Google credential")
            }
            val response =
                request(
                    path = "token?grant_type=id_token",
                    method = HttpMethod.Post,
                    body =
                        buildJsonObject {
                            put("provider", "google")
                            put("id_token", credential.idToken)
                            put("nonce", credential.rawNonce)
                        },
                )
            val session = readTokenResponse(response)
            persistTokens(session)
            AuthSessionData(session.user.id)
        }

    override suspend fun fetchSession(): AuthSessionData? = serialized { restoreSession() }

    private suspend fun restoreSession(): AuthSessionData? {
        val tokens = tokenLocalDataSource.fetchTokens()
        if (tokens.accessToken == null) return null
        val response = request(path = "user", accessToken = tokens.accessToken)
        if (response.status != HttpStatusCode.Unauthorized) return readUser(response)
        val renewed =
            refresh(tokens.refreshToken ?: return clearSession()) ?: return null
        val retried = request(path = "user", accessToken = renewed.accessToken)
        if (isInvalidSession(retried)) return clearSession()
        return readUser(retried)
    }

    override suspend fun deleteAccount() {
        serialized {
            // Once deletion starts, finish the server confirmation and local cleanup together.
            withContext(NonCancellable) {
                val session =
                    restoreSession() ?: throw AuthDataSourceException("Authentication required")
                requestAccountDeletion(session)
                clearSession()
            }
        }
    }

    private suspend fun requestAccountDeletion(session: AuthSessionData) {
        val response =
            request(
                path = "rpc/mvp_delete_account",
                apiPath = "rest/v1",
                method = HttpMethod.Post,
                accessToken = tokenLocalDataSource.fetchTokens().accessToken,
            )
        requireSuccess(response)
        val result = json.decodeFromString<SupabaseDeleteAccountResponse>(response.bodyAsText())
        check(result.deleted && result.userId == session.userId)
    }

    override suspend fun signOut() {
        serialized {
            val accessToken = tokenLocalDataSource.fetchTokens().accessToken
            clearSession()
            if (accessToken == null) return@serialized
            val response =
                request(
                    path = "logout?scope=local",
                    method = HttpMethod.Post,
                    accessToken = accessToken,
                )
            if (!isInvalidSession(response)) requireSuccess(response)
        }
    }

    private suspend fun refresh(refreshToken: String): SupabaseTokenResponse? {
        // Finish token rotation before releasing the lock, including when its caller is cancelled.
        val session = withContext(NonCancellable) { refreshSession(refreshToken) }
        currentCoroutineContext().ensureActive()
        return session
    }

    private suspend fun refreshSession(refreshToken: String): SupabaseTokenResponse? {
        val response =
            request(
                path = "token?grant_type=refresh_token",
                method = HttpMethod.Post,
                body = buildJsonObject { put("refresh_token", refreshToken) },
            )
        if (response.status == HttpStatusCode.BadRequest || isInvalidSession(response)) {
            return clearSession()
        }
        return readTokenResponse(response).also { persistTokens(it) }
    }

    private suspend fun readTokenResponse(response: HttpResponse): SupabaseTokenResponse {
        requireSuccess(response)
        val session = json.decodeFromString<SupabaseTokenResponse>(response.bodyAsText())
        val hasTokens = session.accessToken.isNotBlank() && session.refreshToken.isNotBlank()
        if (!hasTokens || session.user.id.isBlank()) {
            throw AuthDataSourceException("Invalid authentication response")
        }
        return session
    }

    private suspend fun readUser(response: HttpResponse): AuthSessionData? {
        if (isInvalidSession(response)) return clearSession()
        requireSuccess(response)
        val user = json.decodeFromString<SupabaseUserResponse>(response.bodyAsText())
        if (user.id.isBlank()) throw AuthDataSourceException("Invalid authentication response")
        return AuthSessionData(user.id)
    }

    private suspend fun request(
        path: String,
        method: HttpMethod = HttpMethod.Get,
        accessToken: String? = null,
        body: JsonObject? = null,
        apiPath: String = "auth/v1",
    ): HttpResponse {
        if (!publishableKey.startsWith("sb_publishable_")) {
            throw AuthDataSourceException("Configure supabase.publishableKey before authentication")
        }
        return httpClient.request("${projectUrl.trimEnd('/')}/$apiPath/$path") {
            expectSuccess = false
            this.method = method
            header("apikey", publishableKey)
            if (accessToken != null) header(HttpHeaders.Authorization, "Bearer $accessToken")
            if (body != null) {
                contentType(ContentType.Application.Json)
                setBody(body.toString())
            }
        }
    }

    private fun requireSuccess(response: HttpResponse) {
        val statusCode = response.status.value
        if (statusCode !in 200..299) {
            throw AuthDataSourceException("Authentication request failed ($statusCode)")
        }
    }

    private fun isInvalidSession(response: HttpResponse): Boolean =
        when (response.status) {
            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> true
            else -> false
        }

    private suspend fun persistTokens(session: SupabaseTokenResponse) {
        withContext(NonCancellable) {
            tokenLocalDataSource.updateTokens(
                SessionTokenRecord(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                ),
            )
        }
        currentCoroutineContext().ensureActive()
    }

    private suspend fun clearSession(): Nothing? {
        withContext(NonCancellable) { tokenLocalDataSource.updateTokens(SessionTokenRecord()) }
        currentCoroutineContext().ensureActive()
        return null
    }

    private suspend fun <T> serialized(block: suspend () -> T): T =
        try {
            mutex.withLock { block() }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: AuthDataSourceException) {
            currentCoroutineContext().ensureActive()
            throw exception
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
            // HTTP/serialization errors may contain request bodies or raw token responses.
            throw AuthDataSourceException("Authentication operation failed")
        }
}
