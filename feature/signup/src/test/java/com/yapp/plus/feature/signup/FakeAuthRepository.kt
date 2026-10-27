package com.yapp.plus.feature.signup

import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber

internal class FakeAuthRepository : AuthRepository {
    var registrationResult: AuthResult = AuthResult.Failed(AuthFailure.Unavailable)
    var onRegister: suspend () -> AuthResult = { registrationResult }
    val registrations = mutableListOf<Pair<Name, PhoneNumber>>()

    override suspend fun authenticateWithGoogle(credential: GoogleLoginCredential): AuthResult =
        AuthResult.Failed(AuthFailure.Unavailable)

    override suspend fun register(
        registrationToken: String,
        name: Name,
        phoneNumber: PhoneNumber,
    ): AuthResult {
        registrations += name to phoneNumber
        return onRegister()
    }

    override suspend fun hasStoredSession(): Boolean = false
}
