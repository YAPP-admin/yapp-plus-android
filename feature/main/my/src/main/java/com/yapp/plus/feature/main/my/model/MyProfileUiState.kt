package com.yapp.plus.feature.main.my.model

sealed interface MyProfileUiState {
    data object Loading : MyProfileUiState

    data object Empty : MyProfileUiState

    data object Error : MyProfileUiState

    data class Content(
        val profile: MyProfileUiModel,
    ) : MyProfileUiState
}

data class MyProfileUiModel(
    val name: String,
    val phoneNumber: String,
)
