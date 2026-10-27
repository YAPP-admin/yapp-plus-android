package com.yapp.plus.feature.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.RegistrationFieldError
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import com.yapp.plus.feature.signup.model.SignUpStep
import com.yapp.plus.feature.signup.model.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow(restoreState())
        val uiState: StateFlow<SignUpUiState> = mutableUiState.asStateFlow()

        fun updateName(value: String) {
            if (mutableUiState.value.isSubmitting || mutableUiState.value.name == value) return
            savedStateHandle[NAME_KEY] = value
            mutableUiState.update { it.copy(name = value, nameError = null, failure = null) }
        }

        fun updatePhoneNumber(value: String) {
            val state = mutableUiState.value
            if (state.isSubmitting || state.phoneNumber == value) return
            savedStateHandle[PHONE_NUMBER_KEY] = value
            savedStateHandle[CAN_SUBMIT_FROM_NAME_KEY] = false
            mutableUiState.update {
                it.copy(
                    phoneNumber = value,
                    phoneNumberError = null,
                    canSubmitFromName = false,
                    failure = null,
                )
            }
        }

        fun goToNextStep() {
            val state = mutableUiState.value
            if (state.step != SignUpStep.Name || state.isSubmitting) return
            if (state.canSubmitFromName) return
            if (!Name(state.name).isProvided || state.nameError != null) return
            setStep(SignUpStep.Phone)
        }

        suspend fun submit(registrationToken: String?) {
            val state = mutableUiState.value
            val token = registrationToken?.takeIf(String::isNotBlank) ?: return
            val phoneNumber = PhoneNumber(state.phoneNumber).toE164OrNull() ?: return
            val isSubmissionStep =
                state.step == SignUpStep.Phone ||
                    (state.step == SignUpStep.Name && state.canSubmitFromName)
            if (!isSubmissionStep || state.isSubmitting || !Name(state.name).isProvided) return
            if (state.nameError != null || state.phoneNumberError != null) return

            mutableUiState.update { it.copy(isSubmitting = true, failure = null) }
            val result =
                try {
                    authRepository.register(
                        registrationToken = token,
                        name = Name(state.name.trim()),
                        phoneNumber = PhoneNumber(phoneNumber),
                    )
                } catch (exception: CancellationException) {
                    mutableUiState.update { it.copy(isSubmitting = false) }
                    throw exception
                } catch (exception: Exception) {
                    AuthResult.Failed(AuthFailure.Unavailable)
                }

            applyResult(result)
        }

        fun dismissFailure() {
            mutableUiState.update { it.copy(failure = null) }
        }

        fun goBack() {
            val state = mutableUiState.value
            if (state.step == SignUpStep.Phone && !state.isSubmitting) setStep(SignUpStep.Name)
        }

        fun clearSignUpInputs() {
            savedStateHandle.remove<String>(NAME_KEY)
            savedStateHandle.remove<String>(PHONE_NUMBER_KEY)
            savedStateHandle.remove<String>(STEP_KEY)
            savedStateHandle.remove<Boolean>(CAN_SUBMIT_FROM_NAME_KEY)
            mutableUiState.value = SignUpUiState()
        }

        private fun applyResult(result: AuthResult) {
            when (result) {
                AuthResult.SignedIn -> {
                    savedStateHandle[STEP_KEY] = SignUpStep.Complete.name
                    savedStateHandle[CAN_SUBMIT_FROM_NAME_KEY] = false
                    mutableUiState.update {
                        it.copy(
                            step = SignUpStep.Complete,
                            isSubmitting = false,
                            canSubmitFromName = false,
                        )
                    }
                }

                is AuthResult.SignupRequired ->
                    applyFailure(AuthResult.Failed(AuthFailure.AuthenticationRejected))

                is AuthResult.Failed -> applyFailure(result)
            }
        }

        private fun applyFailure(result: AuthResult.Failed) {
            val nameError =
                result.fieldErrors
                    .filterIsInstance<RegistrationFieldError.Name>()
                    .firstOrNull()
                    ?.reason
            val phoneNumberError =
                result.fieldErrors
                    .filterIsInstance<RegistrationFieldError.PhoneNumber>()
                    .firstOrNull()
                    ?.reason
            if (nameError == null && phoneNumberError == null) {
                mutableUiState.update { it.copy(isSubmitting = false, failure = result.reason) }
                return
            }

            val step = if (nameError != null) SignUpStep.Name else SignUpStep.Phone
            val canSubmitFromName = nameError != null && phoneNumberError == null
            savedStateHandle[STEP_KEY] = step.name
            savedStateHandle[CAN_SUBMIT_FROM_NAME_KEY] = canSubmitFromName
            mutableUiState.update {
                it.copy(
                    step = step,
                    isSubmitting = false,
                    failure = null,
                    nameError = nameError,
                    phoneNumberError = phoneNumberError,
                    canSubmitFromName = canSubmitFromName,
                )
            }
        }

        private fun setStep(step: SignUpStep) {
            savedStateHandle[STEP_KEY] = step.name
            mutableUiState.update { it.copy(step = step) }
        }

        private fun restoreStep(): SignUpStep =
            when (savedStateHandle.get<String>(STEP_KEY)) {
                SignUpStep.Phone.name, SignUpStep.Complete.name -> SignUpStep.Phone
                else -> SignUpStep.Name
            }

        private fun restoreState(): SignUpUiState {
            val name = restoreInput(NAME_KEY, LEGACY_NAME_KEY)
            val phoneNumber = restoreInput(PHONE_NUMBER_KEY, LEGACY_PHONE_NUMBER_KEY)
            return SignUpUiState(
                name = name,
                phoneNumber = phoneNumber,
                step = restoreStep(),
                canSubmitFromName =
                    savedStateHandle.get<Boolean>(CAN_SUBMIT_FROM_NAME_KEY) == true &&
                        PhoneNumber(phoneNumber).toE164OrNull() != null,
            )
        }

        private fun restoreInput(
            key: String,
            legacyKey: String,
        ): String {
            val value = savedStateHandle.get<String>(key) ?: savedStateHandle.get<String>(legacyKey)
            savedStateHandle.remove<String>(legacyKey)
            if (value != null) savedStateHandle[key] = value
            return value.orEmpty()
        }

        companion object {
            const val NAME_KEY = "signUpName"
            const val PHONE_NUMBER_KEY = "signUpPhoneNumber"
            const val STEP_KEY = "signUpStep"
            const val CAN_SUBMIT_FROM_NAME_KEY = "signUpCanSubmitFromName"

            private const val LEGACY_NAME_KEY = "authDemoName"
            private const val LEGACY_PHONE_NUMBER_KEY = "authDemoPhoneNumber"
        }
    }
