package com.yapp.plus.feature.login

import androidx.credentials.exceptions.GetCredentialCancellationException
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.feature.login.model.LoginUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {
    @Test
    fun nonceIsFreshAndOnlyItsSha256DigestIsSentToGoogle() {
        val rawNonce = GoogleCredentialManager.generateNonce()
        assertEquals(64, rawNonce.length)
        assertNotEquals(rawNonce, GoogleCredentialManager.generateNonce())
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            GoogleCredentialManager.hashNonce("abc"),
        )
    }

    @Test
    fun duplicateTapReturnsNoCredentialWhileTheFirstRequestIsPending() =
        runTest {
            val credential = GoogleLoginCredential("fake-id-token", "raw-nonce")
            val result = CompletableDeferred<GoogleLoginCredential>()
            val provider =
                FakeGoogleLoginCredentialProvider().apply { request = { result.await() } }
            val viewModel = LoginViewModel(provider)
            val pending =
                async(start = CoroutineStart.UNDISPATCHED) {
                    viewModel.onGoogleLogin(activity = null, serverClientId = "client")
                }
            assertTrue(viewModel.uiState.value.isLoginInProgress)
            assertNull(viewModel.onGoogleLogin(activity = null, serverClientId = "client"))
            result.complete(credential)
            assertSame(credential, pending.await())
            assertEquals(1, provider.requestCount)
            assertEquals(LoginUiState(), viewModel.uiState.value)
        }

    @Test
    fun userCancellationIsSilentAndCoroutineCancellationPropagates() =
        runTest {
            val provider =
                FakeGoogleLoginCredentialProvider().apply {
                    request = { throw GetCredentialCancellationException() }
                }
            val viewModel = LoginViewModel(provider)
            assertNull(viewModel.onGoogleLogin(activity = null, serverClientId = "client"))
            assertEquals(LoginUiState(), viewModel.uiState.value)
            provider.request = { awaitCancellation() }
            val pending =
                async(start = CoroutineStart.UNDISPATCHED) {
                    viewModel.onGoogleLogin(activity = null, serverClientId = "client")
                }
            pending.cancel(CancellationException("rotation"))
            val result = runCatching { pending.await() }
            assertTrue(result.exceptionOrNull() is CancellationException)
            assertEquals(LoginUiState(), viewModel.uiState.value)
        }

    @Test
    fun failureAndTimeoutAllowFreshCredentialRetry() =
        runTest {
            val provider =
                FakeGoogleLoginCredentialProvider().apply { request = { error("provider") } }
            val viewModel = LoginViewModel(provider)
            assertNull(viewModel.onGoogleLogin(activity = null, serverClientId = "client"))
            assertEquals(LoginUiState.Dialog.Error, viewModel.uiState.value.dialog)
            viewModel.dismissDialog()
            provider.request = { awaitCancellation() }
            assertNull(viewModel.onGoogleLogin(activity = null, serverClientId = "client"))
            assertEquals(LoginUiState.Dialog.Error, viewModel.uiState.value.dialog)
            provider.request = { GoogleLoginCredential("retry-token", "retry-nonce") }
            assertEquals(
                "retry-nonce",
                viewModel.onGoogleLogin(activity = null, serverClientId = "client")?.rawNonce,
            )
            assertEquals(LoginUiState(), viewModel.uiState.value)
        }
}
