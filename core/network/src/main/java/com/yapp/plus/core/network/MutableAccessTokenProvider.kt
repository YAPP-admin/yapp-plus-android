package com.yapp.plus.core.network

class MutableAccessTokenProvider : AccessTokenProvider {
    @Volatile
    private var accessToken: String? = BuildConfig.DEBUG_ACCESS_TOKEN.takeIf(String::isNotBlank)

    override fun currentAccessToken(): String? = accessToken

    fun updateAccessToken(value: String?) {
        accessToken = value?.takeIf(String::isNotBlank)
    }
}
