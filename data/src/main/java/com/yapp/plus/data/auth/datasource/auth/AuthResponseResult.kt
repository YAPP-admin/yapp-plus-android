package com.yapp.plus.data.auth.datasource.auth

import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.RegistrationFieldError

sealed interface AuthResponseResult {
    class SignedIn(
        val accessToken: String,
        val refreshToken: String,
    ) : AuthResponseResult

    class SignupRequired(
        val registrationToken: String,
    ) : AuthResponseResult

    data class Failed(
        val reason: AuthFailure,
        val fieldErrors: List<RegistrationFieldError> = emptyList(),
    ) : AuthResponseResult
}
