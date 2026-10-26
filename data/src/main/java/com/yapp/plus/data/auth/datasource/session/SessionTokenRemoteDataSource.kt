package com.yapp.plus.data.auth.datasource.session

import com.yapp.plus.core.network.NetworkRequestError
import com.yapp.plus.core.network.NetworkRequestResult
import com.yapp.plus.core.network.auth.RefreshResult
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.Tokens
import com.yapp.plus.core.network.getOrThrow
import com.yapp.plus.core.network.invokeRequest
import com.yapp.plus.data.auth.model.request.SessionRenewalRequest
import com.yapp.plus.data.auth.model.response.auth.AuthRenewalResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class SessionTokenRemoteDataSource(
    private val httpClient: HttpClient,
) : TokenRefresher {
    override suspend fun refresh(refreshToken: String): RefreshResult {
        val responseResult =
            invokeRequest {
                httpClient.post("auth/session-renewals") {
                    contentType(ContentType.Application.Json)
                    setBody(SessionRenewalRequest(refreshToken))
                }
            }
        if (responseResult.isRejected()) return RefreshResult.Rejected

        val response = responseResult.getOrThrow()
        val body = invokeRequest { response.body<AuthRenewalResponse>() }.getOrThrow()
        return body.toRefreshResult()
    }

    private fun NetworkRequestResult<*>.isRejected(): Boolean =
        when (this) {
            is NetworkRequestResult.Success -> false
            is NetworkRequestResult.Error ->
                (error as? NetworkRequestError.Http)?.statusCode ==
                    HttpStatusCode.Unauthorized.value
        }

    private fun AuthRenewalResponse.toRefreshResult(): RefreshResult {
        check(result == SIGNED_IN_RESULT) { "Unexpected session renewal result" }
        val tokens = checkNotNull(session) { "Missing renewed session" }
        val accessToken =
            checkNotNull(tokens.accessToken.takeIf(String::isNotBlank)) {
                "Missing renewed access token"
            }
        val refreshToken =
            checkNotNull(tokens.refreshToken?.takeIf(String::isNotBlank)) {
                "Missing renewed refresh token"
            }
        return RefreshResult.Success(Tokens(accessToken, refreshToken))
    }

    private companion object {
        const val SIGNED_IN_RESULT = "SIGNED_IN"
    }
}
