package com.yapp.plus.data.auth.datasource.session

import com.yapp.plus.core.network.auth.RefreshResult
import com.yapp.plus.core.network.auth.TokenRefresher
import com.yapp.plus.core.network.auth.Tokens
import com.yapp.plus.data.auth.model.request.SessionRenewalRequest
import com.yapp.plus.data.auth.model.response.auth.AuthRenewalResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
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
            try {
                httpClient.post("auth/session-renewals") {
                    contentType(ContentType.Application.Json)
                    setBody(SessionRenewalRequest(refreshToken))
                }
            } catch (e: ResponseException) {
                if (e.response.status == HttpStatusCode.Unauthorized) {
                    return RefreshResult.Rejected
                }
                throw ResponseException(e.response, e.response.status.description)
            }

        val result = response.body<AuthRenewalResponse>()
        val session = result.session ?: return RefreshResult.Rejected
        val access = session.accessToken.takeIf(String::isNotBlank) ?: return RefreshResult.Rejected
        val refresh = session.refreshToken?.takeIf(String::isNotBlank) ?: return RefreshResult.Rejected
        return RefreshResult.Success(Tokens(access, refresh))
    }
}
