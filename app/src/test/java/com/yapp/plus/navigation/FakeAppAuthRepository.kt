package com.yapp.plus.navigation

import com.yapp.plus.domain.auth.AuthOperationResult
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber

internal class FakeAppAuthRepository(
    var storedSessionExists: Boolean = true,
) : AuthRepository {
    var logoutOperation: suspend () -> AuthOperationResult = { AuthOperationResult.Success }
    var withdrawalOperation: suspend () -> AuthOperationResult = { AuthOperationResult.Success }
    var logoutRequestCount: Int = 0
        private set
    var withdrawalRequestCount: Int = 0
        private set

    override suspend fun hasStoredSession(): Boolean = storedSessionExists

    override suspend fun authenticateWithGoogle(credential: GoogleLoginCredential): AuthResult =
        error("Unexpected Google authentication")

    override suspend fun register(
        registrationToken: String,
        name: Name,
        phoneNumber: PhoneNumber,
    ): AuthResult = error("Unexpected registration")

    override suspend fun logout(): AuthOperationResult {
        logoutRequestCount++
        return logoutOperation()
    }

    override suspend fun withdraw(): AuthOperationResult {
        withdrawalRequestCount++
        return withdrawalOperation()
    }
}
