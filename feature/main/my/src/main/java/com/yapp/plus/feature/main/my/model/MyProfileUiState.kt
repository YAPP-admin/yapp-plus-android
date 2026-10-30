package com.yapp.plus.feature.main.my.model

sealed interface MyProfileUiState {
    data object Unavailable : MyProfileUiState

    data class Content(
        val profile: MyProfileUiModel,
    ) : MyProfileUiState
}
