package com.yapp.plus.notification

internal class FakePushEnvironment : PushEnvironment {
    var permission = true
    var token = "token-a"
    var configured = true
    var installation: suspend () -> String = { "installation" }

    override fun isConfigured(): Boolean = configured

    override fun hasPermission(): Boolean = permission

    override suspend fun fetchInstallationId(): String = installation()

    override suspend fun fetchToken(): String = token
}
