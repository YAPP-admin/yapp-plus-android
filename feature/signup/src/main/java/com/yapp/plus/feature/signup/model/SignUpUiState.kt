package com.yapp.plus.feature.signup.model

import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.RegistrationFieldError

data class SignUpUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val step: SignUpStep = SignUpStep.Name,
    val isSubmitting: Boolean = false,
    val failure: AuthFailure? = null,
    val nameError: RegistrationFieldError.Reason? = null,
    val phoneNumberError: RegistrationFieldError.Reason? = null,
    val canSubmitFromName: Boolean = false,
)
