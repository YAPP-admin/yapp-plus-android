package com.yapp.plus.data.auth.datasource.auth

interface AuthDataSource {
    suspend fun authenticateWithGoogle(
        idToken: String,
        nonce: String,
    ): AuthResponseResult

    suspend fun register(
        registrationToken: String,
        name: String,
        phoneNumber: String,
    ): AuthResponseResult
}
