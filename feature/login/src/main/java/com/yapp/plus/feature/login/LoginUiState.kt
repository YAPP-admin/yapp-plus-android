package com.yapp.plus.feature.login

internal data class LoginUiState(
    val isLoginInProgress: Boolean = false,
    val dialog: Dialog? = null,
) {
    enum class Dialog {
        Error,
        GoogleIdTokenIssued,
    }
}
