package com.yapp.plus.feature.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.profile.ProfileRepository
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.signup.model.SignUpStep
import com.yapp.plus.feature.signup.model.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val profileRepository: ProfileRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow(
                SignUpUiState(
                    name = restoreInput(NAME_KEY, LEGACY_NAME_KEY),
                    phoneNumber = restoreInput(PHONE_NUMBER_KEY, LEGACY_PHONE_NUMBER_KEY),
                    step = restoreStep(),
                ),
            )
        val uiState: StateFlow<SignUpUiState> = mutableUiState.asStateFlow()
        private var saveProfileJob: Job? = null

        fun updateName(value: String) {
            savedStateHandle[NAME_KEY] = value
            mutableUiState.update { it.copy(name = value) }
        }

        fun updatePhoneNumber(value: String) {
            savedStateHandle[PHONE_NUMBER_KEY] = value
            mutableUiState.update { it.copy(phoneNumber = value, profileSaveFailed = false) }
        }

        fun goToNextStep() {
            val currentState = mutableUiState.value
            val nextStep =
                when (currentState.step) {
                    SignUpStep.Name -> {
                        if (!Name(currentState.name).isProvided) return
                        SignUpStep.Phone
                    }

                    SignUpStep.Phone -> {
                        if (currentState.isSavingProfile || !currentState.hasRequiredInputs()) return
                        saveProfileAndComplete(currentState)
                        return
                    }

                    SignUpStep.Complete -> return
                }
            setStep(nextStep)
        }

        fun goBack() {
            saveProfileJob?.cancel()
            saveProfileJob = null
            val previousStep =
                when (mutableUiState.value.step) {
                    SignUpStep.Name -> return
                    SignUpStep.Phone -> SignUpStep.Name
                    SignUpStep.Complete -> SignUpStep.Phone
                }
            mutableUiState.update { it.copy(isSavingProfile = false, profileSaveFailed = false) }
            setStep(previousStep)
        }

        fun clearSignUpInputs() {
            savedStateHandle.remove<String>(NAME_KEY)
            savedStateHandle.remove<String>(PHONE_NUMBER_KEY)
            savedStateHandle.remove<String>(STEP_KEY)
            mutableUiState.value = SignUpUiState()
        }

        private fun setStep(step: SignUpStep) {
            savedStateHandle[STEP_KEY] = step.name
            mutableUiState.update { it.copy(step = step) }
        }

        private fun saveProfileAndComplete(state: SignUpUiState) {
            saveProfileJob =
                viewModelScope.launch {
                    mutableUiState.update { it.copy(isSavingProfile = true, profileSaveFailed = false) }
                    try {
                        profileRepository.saveProfile(
                            Profile(
                                name = Name(state.name),
                                phoneNumber = PhoneNumber(state.phoneNumber),
                            ),
                        )
                        mutableUiState.update {
                            it.copy(
                                step = SignUpStep.Complete,
                                isSavingProfile = false,
                            )
                        }
                        savedStateHandle[STEP_KEY] = SignUpStep.Complete.name
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableUiState.update {
                            it.copy(isSavingProfile = false, profileSaveFailed = true)
                        }
                    }
                }
        }

        private fun SignUpUiState.hasRequiredInputs(): Boolean =
            Name(name).isProvided && PhoneNumber(phoneNumber).isProvided

        private fun restoreStep(): SignUpStep {
            val savedStep = savedStateHandle.get<String>(STEP_KEY) ?: return SignUpStep.Name
            return SignUpStep.entries.firstOrNull { it.name == savedStep } ?: SignUpStep.Name
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
            const val STEP_KEY = "signUpStep"

            private const val LEGACY_NAME_KEY = "authDemoName"
            private const val LEGACY_PHONE_NUMBER_KEY = "authDemoPhoneNumber"
        }
    }
