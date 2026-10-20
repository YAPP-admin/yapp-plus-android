package com.yapp.plus.data.auth

import com.yapp.plus.core.network.auth.RefreshResult
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.Tokens
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class SessionTokenRemoteDataSource(
    private val httpClient: HttpClient,
) : TokenRefresher {
    override suspend fun refresh(refreshToken: String): RefreshResult {
        val response =
            httpClient.post("auth/session-renewals") {
                expectSuccess = false
                contentType(ContentType.Application.Json)
                setBody(SessionRenewalRequest(refreshToken))
            }
        if (response.status == HttpStatusCode.Unauthorized) return RefreshResult.Rejected
        if (response.status != HttpStatusCode.OK) {
            throw ResponseException(response, "Session renewal failed: ${response.status.value}")
        }
        val body = response.body<SessionRenewalResponse>()
        val session = checkNotNull(body.session) { "Missing renewed session" }
        check(body.result == "SIGNED_IN") { "Unexpected session renewal result" }
        return RefreshResult.Success(
            Tokens(session.accessToken, checkNotNull(session.refreshToken)),
        )
    }
}
