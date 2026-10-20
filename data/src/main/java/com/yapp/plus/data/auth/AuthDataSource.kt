package com.yapp.plus.data.auth

import com.yapp.plus.domain.auth.GoogleLoginCredential

interface AuthDataSource {
    suspend fun signInWithGoogle(credential: GoogleLoginCredential): AuthSessionData

    suspend fun fetchSession(): AuthSessionData?

    suspend fun signOut()
}
