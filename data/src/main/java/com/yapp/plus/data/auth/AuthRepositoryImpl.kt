package com.yapp.plus.data.auth

import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthSession
import com.yapp.plus.domain.auth.GoogleLoginCredential

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun signInWithGoogle(credential: GoogleLoginCredential): AuthSession =
        AuthSession(dataSource.signInWithGoogle(credential).userId)

    override suspend fun fetchSession(): AuthSession? {
        val session = dataSource.fetchSession() ?: return null
        return AuthSession(session.userId)
    }

    override suspend fun signOut() = dataSource.signOut()
}
