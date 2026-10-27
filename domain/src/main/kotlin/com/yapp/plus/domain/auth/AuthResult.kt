package com.yapp.plus.domain.auth

sealed interface AuthResult {
    data object SignedIn : AuthResult

    class SignupRequired(
        val registrationToken: String,
    ) : AuthResult {
        override fun toString(): String = "SignupRequired(registrationToken=<redacted>)"
    }

    data class Failed(
        val reason: AuthFailure,
        val fieldErrors: List<RegistrationFieldError> = emptyList(),
    ) : AuthResult
}

enum class AuthFailure {
    InvalidRequest,
    AuthenticationRejected,
    UserAlreadyExists,
    Unavailable,
}
