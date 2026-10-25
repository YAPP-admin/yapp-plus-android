package com.yapp.plus.domain.auth

interface AuthRepository {
    suspend fun signInWithEmail(credential: EmailLoginCredential): AuthSession

    suspend fun signInWithGoogle(credential: GoogleLoginCredential): AuthSession

    /** 복원 가능한 인증 세션이 없으면 null을 반환한다. */
    suspend fun fetchSession(): AuthSession?

    suspend fun signOut()

    /** 현재 앱 계정과 연결 데이터를 삭제한 뒤 로컬 인증 세션을 정리한다. */
    suspend fun deleteAccount()
}
