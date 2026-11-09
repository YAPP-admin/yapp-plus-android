package com.yapp.plus.notification

internal interface PushEnvironment {
    fun isConfigured(): Boolean

    fun hasPermission(): Boolean

    suspend fun fetchInstallationId(): String

    suspend fun fetchToken(): String
}
