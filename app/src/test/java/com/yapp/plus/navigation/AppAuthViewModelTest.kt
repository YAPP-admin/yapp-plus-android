package com.yapp.plus.navigation

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.domain.auth.AuthSession
import com.yapp.plus.domain.auth.GoogleLoginCredential
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppAuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val session = AuthSession("test-user")

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun waitsForRestoreBeforeOpeningMain() =
        runTest(dispatcher) {
            val result = CompletableDeferred<AuthSession?>()
            val repository = FakeAuthRepository().apply { restore = { result.await() } }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            assertEquals(AppAuthUiState.Restoring, viewModel.uiState.value)
            result.complete(session)
            advanceUntilIdle()
            assertEquals(AppAuthUiState.Authenticated(session), viewModel.uiState.value)
        }

    @Test
    fun exchangesOriginalCredentialOnceAndWaitsForTheSession() =
        runTest(dispatcher) {
            val result = CompletableDeferred<AuthSession>()
            val repository = FakeAuthRepository().apply { login = { result.await() } }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            advanceUntilIdle()
            val credential = GoogleLoginCredential("fake-id-token", "raw-nonce")
            repeat(5) { viewModel.signInWithGoogle(credential) }
            assertEquals(AppAuthUiState.Login(isSigningIn = true), viewModel.uiState.value)
            result.complete(session)
            advanceUntilIdle()
            assertSame(credential, repository.credential)
            assertEquals(1, repository.loginCount)
            assertEquals(AppAuthUiState.SignUpRequired(session), viewModel.uiState.value)
            viewModel.completeSignUp()
            assertEquals(AppAuthUiState.Authenticated(session), viewModel.uiState.value)
        }

    @Test
    fun failedRestoreAndExchangeRemainRetryable() =
        runTest(dispatcher) {
            val repository =
                FakeAuthRepository().apply {
                    restore = { error("invalid stored session") }
                    login = { error("sensitive provider response") }
                }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            advanceUntilIdle()
            assertEquals(AppAuthUiState.RestoreError, viewModel.uiState.value)
            repository.restore = { null }
            viewModel.retrySessionRestore()
            advanceUntilIdle()
            val credential = GoogleLoginCredential("fake-id-token", "raw-nonce")
            viewModel.signInWithGoogle(credential)
            advanceUntilIdle()
            assertEquals(AppAuthUiState.Login(hasError = true), viewModel.uiState.value)
            viewModel.dismissError()
            assertEquals(AppAuthUiState.Login(), viewModel.uiState.value)
            repository.login = { session }
            viewModel.signInWithGoogle(credential)
            advanceUntilIdle()
            assertEquals(2, repository.loginCount)
            assertEquals(AppAuthUiState.SignUpRequired(session), viewModel.uiState.value)
        }

    @Test
    fun missingOrCancelledSessionDoesNotAuthenticateOrKeepLoading() =
        runTest(dispatcher) {
            val repository =
                FakeAuthRepository().apply { login = { throw CancellationException() } }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            advanceUntilIdle()
            assertEquals(AppAuthUiState.Login(), viewModel.uiState.value)
            viewModel.signInWithGoogle(GoogleLoginCredential("fake-id-token", "raw-nonce"))
            advanceUntilIdle()
            assertEquals(AppAuthUiState.Login(), viewModel.uiState.value)
        }

    @Test
    fun hungRestoreAndExchangeTimeOutToRetryableLogin() =
        runTest(dispatcher) {
            val repository =
                FakeAuthRepository().apply {
                    restore = { awaitCancellation() }
                    login = { awaitCancellation() }
                }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            advanceUntilIdle()
            assertEquals(AppAuthUiState.RestoreError, viewModel.uiState.value)
            repository.restore = { null }
            viewModel.retrySessionRestore()
            advanceUntilIdle()
            viewModel.signInWithGoogle(GoogleLoginCredential("fake-id-token", "raw-nonce"))
            advanceUntilIdle()
            assertEquals(AppAuthUiState.Login(hasError = true), viewModel.uiState.value)
        }

    @Test
    fun signsOutOnlyOnceAndReturnsToLoginAfterSuccess() =
        runTest(dispatcher) {
            val result = CompletableDeferred<Unit>()
            val repository =
                FakeAuthRepository().apply {
                    restore = { session }
                    logout = { result.await() }
                }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            advanceUntilIdle()

            repeat(3) { viewModel.signOut() }
            runCurrent()

            assertEquals(
                AppAuthUiState.Authenticated(session, isSigningOut = true),
                viewModel.uiState.value,
            )
            assertEquals(1, repository.logoutCount)
            result.complete(Unit)
            advanceUntilIdle()
            assertEquals(AppAuthUiState.Login(), viewModel.uiState.value)
        }

    @Test
    fun failedSignOutKeepsSessionAndCanDismissError() =
        runTest(dispatcher) {
            val repository =
                FakeAuthRepository().apply {
                    restore = { session }
                    logout = { error("provider response") }
                }
            val viewModel = AppAuthViewModel(repository, SavedStateHandle())
            advanceUntilIdle()

            viewModel.signOut()
            advanceUntilIdle()

            assertEquals(
                AppAuthUiState.Authenticated(session, hasSignOutError = true),
                viewModel.uiState.value,
            )
            viewModel.dismissSignOutError()
            assertEquals(AppAuthUiState.Authenticated(session), viewModel.uiState.value)
        }
}
