package com.yapp.plus.data.auth

import com.yapp.plus.data.auth.datasource.auth.AuthDataSource
import com.yapp.plus.data.auth.datasource.auth.AuthResponseResult

internal class FakeAuthDataSource : AuthDataSource {
    var authenticationResult: AuthResponseResult? = null
    var operation: suspend () -> Unit = {}
    var logoutRequestCount: Int = 0
        private set
    var withdrawalRequestCount: Int = 0
        private set

    override suspend fun authenticateWithGoogle(
        idToken: String,
        nonce: String,
    ): AuthResponseResult = checkNotNull(authenticationResult)

    override suspend fun register(
        registrationToken: String,
        name: String,
        phoneNumber: String,
    ): AuthResponseResult = checkNotNull(authenticationResult)

    override suspend fun logout(refreshToken: String) {
        logoutRequestCount++
        operation()
    }

    override suspend fun withdraw() {
        withdrawalRequestCount++
        operation()
    }
}
