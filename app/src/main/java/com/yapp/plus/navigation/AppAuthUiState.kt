package com.yapp.plus.navigation

import com.yapp.plus.domain.auth.MembershipReviewStatus

internal data class AppAuthUiState(
    val isCheckingSession: Boolean = true,
    val hasSession: Boolean = false,
    val membershipReviewStatus: MembershipReviewStatus? = null,
    val registrationToken: String? = null,
    val isLoggingOut: Boolean = false,
    val hasLogoutError: Boolean = false,
    val isWithdrawing: Boolean = false,
    val hasWithdrawalError: Boolean = false,
) {
    override fun toString(): String =
        "AppAuthUiState(isCheckingSession=$isCheckingSession, hasSession=$hasSession, " +
            "membershipReviewStatus=$membershipReviewStatus, " +
            "registrationToken=<redacted>, isLoggingOut=$isLoggingOut, " +
            "hasLogoutError=$hasLogoutError, isWithdrawing=$isWithdrawing, " +
            "hasWithdrawalError=$hasWithdrawalError)"
}
