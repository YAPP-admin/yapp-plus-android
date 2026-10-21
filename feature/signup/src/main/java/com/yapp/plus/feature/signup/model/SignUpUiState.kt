package com.yapp.plus.feature.signup.model

data class SignUpUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val step: SignUpStep = SignUpStep.Name,
    val isSavingProfile: Boolean = false,
    val profileSaveFailed: Boolean = false,
)
