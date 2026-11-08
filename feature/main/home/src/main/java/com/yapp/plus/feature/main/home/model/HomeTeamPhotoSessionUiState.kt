package com.yapp.plus.feature.main.home.model

internal sealed interface HomeTeamPhotoSessionUiState {
    data object AwaitingPhoto : HomeTeamPhotoSessionUiState

    data class Pending(
        val uploadTimeLabel: String,
        val uploaderName: String,
        val attendeeCount: Int,
    ) : HomeTeamPhotoSessionUiState

    data object Approved : HomeTeamPhotoSessionUiState
}
