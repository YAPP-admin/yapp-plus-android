package com.yapp.plus.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthSession
import com.yapp.plus.domain.auth.GoogleLoginCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val SESSION_REQUEST_TIMEOUT_MILLIS = 100_000L

@HiltViewModel
internal class AppAuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow<AppAuthUiState>(AppAuthUiState.Restoring)
        val uiState = mutableUiState.asStateFlow()

        init {
            viewModelScope.launch { resolveSession(authRepository::fetchSession) }
        }

        fun retrySessionRestore() {
            if (mutableUiState.value != AppAuthUiState.RestoreError) return
            mutableUiState.value = AppAuthUiState.Restoring
            viewModelScope.launch { resolveSession(authRepository::fetchSession) }
        }

        fun signInWithGoogle(credential: GoogleLoginCredential) {
            val state = mutableUiState.value as? AppAuthUiState.Login ?: return
            if (state.isSigningIn) return
            mutableUiState.value = AppAuthUiState.Login(isSigningIn = true)
            viewModelScope.launch { resolveSession { authRepository.signInWithGoogle(credential) } }
        }

        fun dismissError() {
            if (mutableUiState.value == AppAuthUiState.RestoreError) {
                mutableUiState.value = AppAuthUiState.Login()
                return
            }
            val state = mutableUiState.value as? AppAuthUiState.Login ?: return
            mutableUiState.value = state.copy(hasError = false)
        }

        private suspend fun resolveSession(request: suspend () -> AuthSession?) {
            val failureState =
                if (mutableUiState.value == AppAuthUiState.Restoring) {
                    AppAuthUiState.RestoreError
                } else {
                    AppAuthUiState.Login(hasError = true)
                }
            try {
                mutableUiState.value =
                    withTimeoutOrNull(SESSION_REQUEST_TIMEOUT_MILLIS) {
                        request()?.let(AppAuthUiState::Authenticated) ?: AppAuthUiState.Login()
                    } ?: failureState
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                mutableUiState.value = failureState
            } finally {
                clearLoading()
            }
        }

        private fun clearLoading() {
            val state = mutableUiState.value
            if (state == AppAuthUiState.Restoring ||
                state is AppAuthUiState.Login &&
                state.isSigningIn
            ) {
                mutableUiState.value = AppAuthUiState.Login()
            }
        }
    }
