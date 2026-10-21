package com.yapp.plus.navigation

import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthSession
import com.yapp.plus.domain.auth.GoogleLoginCredential

internal class FakeAuthRepository : AuthRepository {
    var restore: suspend () -> AuthSession? = { null }
    var login: suspend () -> AuthSession = { AuthSession("test-user") }
    var logout: suspend () -> Unit = {}
    var delete: suspend () -> Unit = {}
    var loginCount = 0
    var logoutCount = 0
    var deleteCount = 0
    var credential: GoogleLoginCredential? = null

    override suspend fun fetchSession(): AuthSession? = restore()

    override suspend fun signInWithGoogle(credential: GoogleLoginCredential): AuthSession {
        loginCount += 1
        this.credential = credential
        return login()
    }

    override suspend fun signOut() {
        logoutCount += 1
        logout()
    }

    override suspend fun deleteAccount() {
        deleteCount += 1
        delete()
    }
}
