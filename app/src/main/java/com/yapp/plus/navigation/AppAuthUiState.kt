package com.yapp.plus.navigation

import com.yapp.plus.domain.auth.AuthSession

internal sealed interface AppAuthUiState {
    data object Restoring : AppAuthUiState

    data object RestoreError : AppAuthUiState

    data class Login(
        val isSigningIn: Boolean = false,
        val hasError: Boolean = false,
    ) : AppAuthUiState

    data class Authenticated(
        val session: AuthSession,
    ) : AppAuthUiState

    data class SignUpRequired(
        val session: AuthSession,
    ) : AppAuthUiState
}
