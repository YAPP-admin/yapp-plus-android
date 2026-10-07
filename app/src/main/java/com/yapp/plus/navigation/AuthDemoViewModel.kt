package com.yapp.plus.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class AuthDemoViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow(
                AuthDemoUiState(
                    name = savedStateHandle[NAME_KEY] ?: "",
                    phoneNumber = savedStateHandle[PHONE_NUMBER_KEY] ?: "",
                ),
            )
        val uiState: StateFlow<AuthDemoUiState> = mutableUiState.asStateFlow()

        fun updateName(value: String) {
            savedStateHandle[NAME_KEY] = value
            mutableUiState.update { it.copy(name = value) }
        }

        fun updatePhoneNumber(value: String) {
            savedStateHandle[PHONE_NUMBER_KEY] = value
            mutableUiState.update { it.copy(phoneNumber = value) }
        }

        internal companion object {
            const val NAME_KEY = "authDemoName"
            const val PHONE_NUMBER_KEY = "authDemoPhoneNumber"
        }
    }
