package com.yapp.plus.core.network.auth

fun interface TokenRefresher {
    suspend fun refresh(refreshToken: String): RefreshResult
}
