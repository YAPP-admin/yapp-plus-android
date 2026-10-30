package com.yapp.plus.data.auth

import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.data.auth.datasource.auth.AuthResponseResult
import com.yapp.plus.data.auth.datasource.session.SessionProfileDataSource
import com.yapp.plus.data.auth.datasource.session.SessionTokenLocalDataSource
import com.yapp.plus.data.auth.repository.AuthRepositoryImpl
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthProfileIntegrationTest {
    @Test fun loginAndSignupSaveServerNameAndLogoutRemovesIt() =
        runTest {
            val local = SessionTokenLocalDataSource(FakeSessionRecordStore())
            val tokens = TokenStore(persistence = local)
            val remote =
                FakeAuthDataSource().apply {
                    authenticationResult = AuthResponseResult.SignedIn("access", "refresh", "서버 이름")
                }
            val repository = AuthRepositoryImpl(remote, tokens, local)
            assertEquals(
                AuthResult.SignedIn,
                repository.authenticateWithGoogle(GoogleLoginCredential("token", "nonce")),
            )
            assertEquals("서버 이름", local.fetchName())
            repository.logout()
            assertNull(local.fetchName())
            assertNull(tokens.currentTokens())
            assertEquals(
                AuthResult.SignedIn,
                repository.register("reg", Name("입력 이름"), PhoneNumber("01012345678")),
            )
            assertEquals("서버 이름", local.fetchName())
        }

    @Test fun profileStorageFailureRollsBackTokens() =
        runTest {
            val tokens = TokenStore()
            val remote =
                FakeAuthDataSource().apply {
                    authenticationResult = AuthResponseResult.SignedIn("access", "refresh", "이름")
                }
            val profile =
                object : SessionProfileDataSource {
                    override suspend fun fetchName(): String? = null

                    override suspend fun updateName(
                        storageKey: String,
                        name: String?,
                    ): Boolean = error("disk failure")
                }
            val repository = AuthRepositoryImpl(remote, tokens, profile)
            assertTrue(
                repository.authenticateWithGoogle(
                    GoogleLoginCredential("token", "nonce"),
                ) is AuthResult.Failed,
            )
            assertNull(tokens.currentTokens())
        }
}
