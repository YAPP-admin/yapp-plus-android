package com.yapp.plus.feature.login

import android.app.Activity
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.feature.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel
    @Inject
    constructor(
        private val googleLoginCredentialProvider: GoogleLoginCredentialProvider,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(LoginUiState())
        val uiState = mutableUiState.asStateFlow()

        suspend fun onGoogleLogin(
            activity: Activity?,
            serverClientId: String,
        ): GoogleLoginCredential? {
            if (mutableUiState.value.isLoginInProgress) return null
            mutableUiState.value = LoginUiState(isLoginInProgress = true)
            return try {
                requestGoogleCredential(activity, serverClientId)
            } catch (exception: Exception) {
                handleCredentialRequestFailure(exception)
                null
            } finally {
                mutableUiState.update { it.copy(isLoginInProgress = false) }
            }
        }

        fun dismissDialog() {
            mutableUiState.update { it.copy(dialog = null) }
        }

        private suspend fun requestGoogleCredential(
            activity: Activity?,
            serverClientId: String,
        ): GoogleLoginCredential? {
            val credential =
                withTimeoutOrNull(CREDENTIAL_REQUEST_TIMEOUT_MILLIS) {
                    googleLoginCredentialProvider.getGoogleLoginCredential(activity, serverClientId)
                }
            if (credential == null) showError()
            return credential
        }

        private fun handleCredentialRequestFailure(exception: Exception) {
            when (exception) {
                is GetCredentialCancellationException -> Unit
                is CancellationException -> throw exception
                else -> showError()
            }
        }

        private fun showError() {
            mutableUiState.update { it.copy(dialog = LoginUiState.Dialog.Error) }
        }

        private companion object {
            const val CREDENTIAL_REQUEST_TIMEOUT_MILLIS = 120_000L
        }
    }
