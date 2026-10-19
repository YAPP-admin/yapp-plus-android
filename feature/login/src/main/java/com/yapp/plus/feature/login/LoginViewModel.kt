package com.yapp.plus.feature.login

import android.app.Activity
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.feature.login.model.GoogleLoginCredential
import com.yapp.plus.feature.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel
    @Inject
    constructor(
        private val googleLoginCredentialProvider: GoogleLoginCredentialProvider,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(LoginUiState())
        val uiState = mutableUiState.asStateFlow()

        private val mutableCredentialEvents = Channel<GoogleLoginCredential>(Channel.BUFFERED)
        val credentialEvents = mutableCredentialEvents.receiveAsFlow()

        fun onGoogleLogin(
            activity: Activity?,
            serverClientId: String,
        ) {
            if (mutableUiState.value.isLoginInProgress) return

            mutableUiState.update {
                it.copy(
                    isLoginInProgress = true,
                    dialog = null,
                )
            }
            viewModelScope.launch {
                requestGoogleCredential(activity, serverClientId)
            }
        }

        fun dismissDialog() {
            mutableUiState.update { it.copy(dialog = null) }
        }

        private suspend fun requestGoogleCredential(
            activity: Activity?,
            serverClientId: String,
        ) {
            try {
                issueGoogleCredential(activity, serverClientId)
            } catch (exception: Exception) {
                handleCredentialRequestFailure(exception)
            } finally {
                finishLogin()
            }
        }

        private suspend fun issueGoogleCredential(
            activity: Activity?,
            serverClientId: String,
        ) {
            val credential =
                googleLoginCredentialProvider.getGoogleLoginCredential(
                    activity = checkNotNull(activity),
                    serverClientId = serverClientId,
                )
            mutableCredentialEvents.send(credential)
            mutableUiState.update { it.copy(dialog = LoginUiState.Dialog.GoogleIdTokenIssued) }
        }

        private fun handleCredentialRequestFailure(exception: Exception) {
            when (exception) {
                is GetCredentialCancellationException -> Unit
                is CancellationException -> throw exception
                else -> mutableUiState.update { it.copy(dialog = LoginUiState.Dialog.Error) }
            }
        }

        private fun finishLogin() {
            mutableUiState.update { it.copy(isLoginInProgress = false) }
        }
    }
