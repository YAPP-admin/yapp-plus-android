package com.yapp.plus.feature.main.my.model

sealed interface MyProfileUiState {
    data object Unavailable : MyProfileUiState

    data class Loading(
        val savedName: String? = null,
    ) : MyProfileUiState

    data class Error(
        val savedName: String? = null,
    ) : MyProfileUiState

    data class Content(
        val profile: MyProfileUiModel,
    ) : MyProfileUiState
}
