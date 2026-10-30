package com.yapp.plus.data.auth.model.response.auth

import com.yapp.plus.data.auth.datasource.auth.AuthResponseResult
import com.yapp.plus.domain.auth.AuthFailure
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthenticationResponse(
    val result: String,
    val session: AuthTokensResponse? = null,
    val registrationToken: String? = null,
    val user: AuthenticatedUserResponse? = null,
) {
    fun toResult(): AuthResponseResult =
        when (result) {
            RESULT_SIGNED_IN -> toSignedInResult()
            RESULT_SIGNUP_REQUIRED -> toSignupRequiredResult()
            else -> AuthResponseResult.Failed(AuthFailure.Unavailable)
        }

    private fun toSignedInResult(): AuthResponseResult {
        val activeSession = session
        val accessToken = activeSession?.accessToken?.takeIf(String::isNotBlank)
        val refreshToken = activeSession?.refreshToken?.takeIf(String::isNotBlank)
        return if (accessToken == null || refreshToken == null) {
            AuthResponseResult.Failed(AuthFailure.Unavailable)
        } else {
            AuthResponseResult.SignedIn(
                accessToken,
                refreshToken,
                user?.name?.takeIf(String::isNotBlank),
            )
        }
    }

    private fun toSignupRequiredResult(): AuthResponseResult {
        val token = registrationToken?.takeIf(String::isNotBlank)
        return if (token == null) {
            AuthResponseResult.Failed(AuthFailure.Unavailable)
        } else {
            AuthResponseResult.SignupRequired(token)
        }
    }

    companion object {
        const val RESULT_SIGNED_IN = "SIGNED_IN"
        const val RESULT_SIGNUP_REQUIRED = "SIGNUP_REQUIRED"
    }
}
