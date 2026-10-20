package com.yapp.plus.feature.login.model

internal data class LoginUiState(
    val isLoginInProgress: Boolean = false,
    val dialog: Dialog? = null,
) {
    enum class Dialog {
        Error,
    }
}
