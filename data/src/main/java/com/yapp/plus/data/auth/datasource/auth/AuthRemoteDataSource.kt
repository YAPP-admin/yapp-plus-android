package com.yapp.plus.data.auth.datasource.auth

import com.yapp.plus.data.auth.model.request.auth.GoogleNativeAuthenticationRequest
import com.yapp.plus.data.auth.model.request.auth.UserRegistrationRequest
import com.yapp.plus.data.auth.model.response.ApiErrorResponse
import com.yapp.plus.data.auth.model.response.auth.AuthenticationResponse
import com.yapp.plus.domain.auth.AuthFailure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthRemoteDataSource(
    private val httpClient: HttpClient,
) : AuthDataSource {
    override suspend fun authenticateWithGoogle(
        idToken: String,
        nonce: String,
    ): AuthResponseResult =
        post(OIDC_GOOGLE_AUTHENTICATIONS_PATH) {
            setBody(GoogleNativeAuthenticationRequest(idToken, nonce))
        }

    override suspend fun register(
        registrationToken: String,
        name: String,
        phoneNumber: String,
    ): AuthResponseResult =
        post(USERS_PATH) {
            setBody(UserRegistrationRequest(registrationToken, name, phoneNumber))
        }

    private suspend fun post(
        path: String,
        setRequestBody: HttpRequestBuilder.() -> Unit,
    ): AuthResponseResult {
        val response =
            httpClient.post(path) {
                expectSuccess = false
                contentType(ContentType.Application.Json)
                setRequestBody()
            }
        if (response.status != HttpStatusCode.OK) {
            return readFailure(response.body<ApiErrorResponse>())
        }

        return response.body<AuthenticationResponse>().toResult()
    }

    private fun readFailure(error: ApiErrorResponse): AuthResponseResult.Failed {
        val reason =
            when (error.code) {
                ERROR_INVALID_REQUEST -> AuthFailure.InvalidRequest
                ERROR_AUTHENTICATION_FAILED, ERROR_AUTHENTICATION_STATE_INVALID ->
                    AuthFailure.AuthenticationRejected

                ERROR_USER_ALREADY_EXISTS -> AuthFailure.UserAlreadyExists
                else -> AuthFailure.Unavailable
            }
        val fieldErrors =
            if (reason == AuthFailure.InvalidRequest || reason == AuthFailure.UserAlreadyExists) {
                error.fieldErrors.mapNotNull { it.toDomain() }
            } else {
                emptyList()
            }
        return AuthResponseResult.Failed(reason, fieldErrors)
    }

    private companion object {
        const val OIDC_GOOGLE_AUTHENTICATIONS_PATH = "auth/oidc/google/native-authentications"
        const val USERS_PATH = "users"

        const val ERROR_INVALID_REQUEST = "INVALID_REQUEST"
        const val ERROR_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED"
        const val ERROR_AUTHENTICATION_STATE_INVALID = "AUTHENTICATION_STATE_INVALID"
        const val ERROR_USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS"
    }
}
