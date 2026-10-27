package com.yapp.plus.domain.auth

import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber

interface AuthRepository {
    suspend fun authenticateWithGoogle(credential: GoogleLoginCredential): AuthResult

    suspend fun register(
        registrationToken: String,
        name: Name,
        phoneNumber: PhoneNumber,
    ): AuthResult

    suspend fun hasStoredSession(): Boolean

    suspend fun logout(): AuthOperationResult = AuthOperationResult.NotSupported

    suspend fun withdraw(): AuthOperationResult = AuthOperationResult.NotSupported
}
