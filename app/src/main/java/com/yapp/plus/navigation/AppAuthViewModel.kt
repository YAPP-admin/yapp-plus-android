package com.yapp.plus.navigation

import androidx.lifecycle.SavedStateHandle
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
import kotlin.time.Duration.Companion.milliseconds

private const val SESSION_REQUEST_TIMEOUT_MILLIS = 100_000L

@HiltViewModel
internal class AppAuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow<AppAuthUiState>(AppAuthUiState.Restoring)
        val uiState = mutableUiState.asStateFlow()

        init {
            viewModelScope.launch {
                resolveSession(
                    request = authRepository::fetchSession,
                    requiresSignUp = savedStateHandle[REQUIRES_SIGN_UP_KEY] ?: false,
                )
            }
        }

        fun retrySessionRestore() {
            if (mutableUiState.value != AppAuthUiState.RestoreError) return
            mutableUiState.value = AppAuthUiState.Restoring
            viewModelScope.launch {
                resolveSession(
                    request = authRepository::fetchSession,
                    requiresSignUp = savedStateHandle[REQUIRES_SIGN_UP_KEY] ?: false,
                )
            }
        }

        fun signInWithGoogle(credential: GoogleLoginCredential) {
            val state = mutableUiState.value as? AppAuthUiState.Login ?: return
            if (state.isSigningIn) return
            mutableUiState.value = AppAuthUiState.Login(isSigningIn = true)
            viewModelScope.launch {
                resolveSession(
                    request = { authRepository.signInWithGoogle(credential) },
                    requiresSignUp = true,
                )
            }
        }

        fun completeSignUp() {
            val state = mutableUiState.value as? AppAuthUiState.SignUpRequired ?: return
            savedStateHandle.remove<Boolean>(REQUIRES_SIGN_UP_KEY)
            mutableUiState.value = AppAuthUiState.Authenticated(state.session)
        }

        fun signOut() {
            val state = mutableUiState.value as? AppAuthUiState.Authenticated ?: return
            if (state.isSigningOut || state.isDeletingAccount) return
            mutableUiState.value = state.copy(isSigningOut = true, hasSignOutError = false)
            viewModelScope.launch {
                try {
                    authRepository.signOut()
                    mutableUiState.value = AppAuthUiState.Login()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    val currentState =
                        mutableUiState.value as? AppAuthUiState.Authenticated ?: return@launch
                    val currentSession =
                        try {
                            authRepository.fetchSession()
                        } catch (exception: CancellationException) {
                            throw exception
                        } catch (_: Exception) {
                            mutableUiState.value =
                                currentState.copy(isSigningOut = false, hasSignOutError = true)
                            return@launch
                        }
                    mutableUiState.value =
                        if (currentSession == null) {
                            AppAuthUiState.Login()
                        } else {
                            AppAuthUiState.Authenticated(
                                session = currentSession,
                                hasSignOutError = true,
                            )
                        }
                }
            }
        }

        fun dismissSignOutError() {
            val state = mutableUiState.value as? AppAuthUiState.Authenticated ?: return
            if (!state.hasSignOutError) return
            mutableUiState.value = state.copy(hasSignOutError = false)
        }

        fun deleteAccount() {
            val state = mutableUiState.value as? AppAuthUiState.Authenticated ?: return
            if (state.isDeletingAccount || state.isSigningOut) return
            mutableUiState.value = state.copy(isDeletingAccount = true, hasDeleteAccountError = false)
            viewModelScope.launch {
                try {
                    authRepository.deleteAccount()
                    savedStateHandle.remove<Boolean>(REQUIRES_SIGN_UP_KEY)
                    mutableUiState.value = AppAuthUiState.Login()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    recoverDeletionFailure(state)
                } finally {
                    val current = mutableUiState.value as? AppAuthUiState.Authenticated
                    if (current?.isDeletingAccount == true) {
                        mutableUiState.value = current.copy(isDeletingAccount = false)
                    }
                }
            }
        }

        fun dismissDeleteAccountError() {
            val state = mutableUiState.value as? AppAuthUiState.Authenticated ?: return
            mutableUiState.value = state.copy(hasDeleteAccountError = false)
        }

        private suspend fun recoverDeletionFailure(state: AppAuthUiState.Authenticated) {
            val session =
                try {
                    authRepository.fetchSession()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    state.session
                }
            mutableUiState.value =
                if (session == null) {
                    AppAuthUiState.Login()
                } else {
                    AppAuthUiState.Authenticated(session, hasDeleteAccountError = true)
                }
        }

        fun dismissError() {
            if (mutableUiState.value == AppAuthUiState.RestoreError) {
                mutableUiState.value = AppAuthUiState.Login()
                return
            }
            val state = mutableUiState.value as? AppAuthUiState.Login ?: return
            mutableUiState.value = state.copy(hasError = false)
        }

        private suspend fun resolveSession(
            request: suspend () -> AuthSession?,
            requiresSignUp: Boolean = false,
        ) {
            val failureState =
                if (mutableUiState.value == AppAuthUiState.Restoring) {
                    AppAuthUiState.RestoreError
                } else {
                    AppAuthUiState.Login(hasError = true)
                }
            var requestCompleted = false
            try {
                val session =
                    withTimeoutOrNull(SESSION_REQUEST_TIMEOUT_MILLIS.milliseconds) {
                        request().also { requestCompleted = true }
                    }
                mutableUiState.value =
                    when {
                        !requestCompleted -> failureState
                        session == null -> AppAuthUiState.Login()
                        requiresSignUp -> {
                            savedStateHandle[REQUIRES_SIGN_UP_KEY] = true
                            AppAuthUiState.SignUpRequired(session)
                        }
                        else -> AppAuthUiState.Authenticated(session)
                    }
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

        private companion object {
            const val REQUIRES_SIGN_UP_KEY = "requiresSignUp"
        }
    }
