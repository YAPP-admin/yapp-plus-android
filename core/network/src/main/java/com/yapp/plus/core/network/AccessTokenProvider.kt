package com.yapp.plus.core.network

fun interface AccessTokenProvider {
    fun currentAccessToken(): String?
}
