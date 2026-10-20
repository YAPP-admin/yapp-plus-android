package com.yapp.plus.core.network.auth

sealed interface RefreshResult {
    data class Success(
        val tokens: Tokens,
    ) : RefreshResult

    data object Rejected : RefreshResult
}
