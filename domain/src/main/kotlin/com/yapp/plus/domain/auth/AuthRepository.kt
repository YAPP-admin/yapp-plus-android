package com.yapp.plus.domain.auth

interface AuthRepository {
    suspend fun signInWithGoogle(credential: GoogleLoginCredential): AuthSession

    /** 복원 가능한 인증 세션이 없으면 null을 반환한다. */
    suspend fun fetchSession(): AuthSession?

    suspend fun signOut()
}
