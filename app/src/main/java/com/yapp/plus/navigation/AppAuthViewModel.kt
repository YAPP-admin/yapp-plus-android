package com.yapp.plus.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.AuthOperationResult
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AppAuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(AppAuthUiState())
        val uiState = mutableUiState.asStateFlow()

        init {
            viewModelScope.launch {
                val hasSession =
                    try {
                        authRepository.hasStoredSession()
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (exception: Exception) {
                        false
                    }
                mutableUiState.update {
                    it.copy(isCheckingSession = false, hasSession = hasSession)
                }
            }
        }

        suspend fun authenticateWithGoogle(credential: GoogleLoginCredential): AuthResult {
            val result =
                try {
                    authRepository.authenticateWithGoogle(credential)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    AuthResult.Failed(AuthFailure.Unavailable)
                }
            when (result) {
                AuthResult.SignedIn ->
                    mutableUiState.update {
                        it.copy(hasSession = true, registrationToken = null)
                    }

                is AuthResult.SignupRequired ->
                    mutableUiState.update {
                        it.copy(registrationToken = result.registrationToken)
                    }

                is AuthResult.Failed ->
                    mutableUiState.update {
                        it.copy(registrationToken = null)
                    }
            }
            return result
        }

        fun onRegistrationCompleted() {
            mutableUiState.update {
                it.copy(hasSession = true, registrationToken = null)
            }
        }

        fun logout() {
            performSessionOperation(
                isLogout = true,
                operation = authRepository::logout,
            )
        }

        fun withdraw() {
            performSessionOperation(
                isLogout = false,
                operation = authRepository::withdraw,
            )
        }

        fun onLogoutErrorDismissed() {
            mutableUiState.update { it.copy(hasLogoutError = false) }
        }

        fun onWithdrawalErrorDismissed() {
            mutableUiState.update { it.copy(hasWithdrawalError = false) }
        }

        private fun performSessionOperation(
            isLogout: Boolean,
            operation: suspend () -> AuthOperationResult,
        ) {
            val currentState = mutableUiState.value
            if (
                !currentState.hasSession || currentState.isLoggingOut || currentState.isWithdrawing
            ) {
                return
            }

            startSessionOperation(isLogout)
            viewModelScope.launch {
                try {
                    updateSessionOperationResult(
                        result = executeSessionOperation(operation),
                        isLogout = isLogout,
                    )
                } finally {
                    mutableUiState.update {
                        it.copy(isLoggingOut = false, isWithdrawing = false)
                    }
                }
            }
        }

        private fun startSessionOperation(isLogout: Boolean) {
            mutableUiState.update {
                it.copy(
                    isLoggingOut = isLogout,
                    hasLogoutError = false,
                    isWithdrawing = !isLogout,
                    hasWithdrawalError = false,
                )
            }
        }

        private suspend fun executeSessionOperation(
            operation: suspend () -> AuthOperationResult,
        ): AuthOperationResult =
            try {
                operation()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                AuthOperationResult.Failure
            }

        private fun updateSessionOperationResult(
            result: AuthOperationResult,
            isLogout: Boolean,
        ) {
            when (result) {
                AuthOperationResult.Success ->
                    mutableUiState.update {
                        it.copy(hasSession = false, registrationToken = null)
                    }

                AuthOperationResult.Failure, AuthOperationResult.NotSupported ->
                    mutableUiState.update {
                        it.copy(hasLogoutError = isLogout, hasWithdrawalError = !isLogout)
                    }
            }
        }
    }
