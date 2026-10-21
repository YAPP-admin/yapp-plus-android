package com.yapp.plus.feature.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow(
                SignUpUiState(
                    name = restoreInput(NAME_KEY, LEGACY_NAME_KEY),
                    phoneNumber = restoreInput(PHONE_NUMBER_KEY, LEGACY_PHONE_NUMBER_KEY),
                ),
            )
        val uiState: StateFlow<SignUpUiState> = mutableUiState.asStateFlow()

        fun updateName(value: String) {
            savedStateHandle[NAME_KEY] = value
            mutableUiState.update { it.copy(name = value) }
        }

        fun updatePhoneNumber(value: String) {
            savedStateHandle[PHONE_NUMBER_KEY] = value
            mutableUiState.update { it.copy(phoneNumber = value) }
        }

        fun clearSignUpInputs() {
            savedStateHandle.remove<String>(NAME_KEY)
            savedStateHandle.remove<String>(PHONE_NUMBER_KEY)
            mutableUiState.value = SignUpUiState()
        }

        private fun restoreInput(
            key: String,
            legacyKey: String,
        ): String {
            val value =
                savedStateHandle.get<String>(key)
                    ?: savedStateHandle.get<String>(legacyKey)
            savedStateHandle.remove<String>(legacyKey)
            if (value != null) savedStateHandle[key] = value
            return value.orEmpty()
        }

        companion object {
            const val NAME_KEY = "signUpName"
            const val PHONE_NUMBER_KEY = "signUpPhoneNumber"

            private const val LEGACY_NAME_KEY = "authDemoName"
            private const val LEGACY_PHONE_NUMBER_KEY = "authDemoPhoneNumber"
        }
    }
