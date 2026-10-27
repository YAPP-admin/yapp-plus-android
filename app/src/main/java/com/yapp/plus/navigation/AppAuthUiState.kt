package com.yapp.plus.navigation

internal data class AppAuthUiState(
    val isCheckingSession: Boolean = true,
    val hasSession: Boolean = false,
    val registrationToken: String? = null,
    val isLoggingOut: Boolean = false,
    val hasLogoutError: Boolean = false,
    val isWithdrawing: Boolean = false,
    val hasWithdrawalError: Boolean = false,
) {
    override fun toString(): String =
        "AppAuthUiState(isCheckingSession=$isCheckingSession, hasSession=$hasSession, " +
            "registrationToken=<redacted>, isLoggingOut=$isLoggingOut, " +
            "hasLogoutError=$hasLogoutError, isWithdrawing=$isWithdrawing, " +
            "hasWithdrawalError=$hasWithdrawalError)"
}
