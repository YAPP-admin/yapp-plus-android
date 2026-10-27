package com.yapp.plus.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.auth.AuthFailure
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

internal data class AppAuthUiState(
    val isCheckingSession: Boolean = true,
    val hasSession: Boolean = false,
    val registrationToken: String? = null,
) {
    override fun toString(): String =
        "AppAuthUiState(isCheckingSession=$isCheckingSession, hasSession=$hasSession, registrationToken=<redacted>)"
}

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
    }
