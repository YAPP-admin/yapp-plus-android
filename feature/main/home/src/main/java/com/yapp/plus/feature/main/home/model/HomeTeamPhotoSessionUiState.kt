package com.yapp.plus.feature.main.home.model

internal sealed interface HomeTeamPhotoSessionUiState {
    data object Loading : HomeTeamPhotoSessionUiState

    data object LookupFailed : HomeTeamPhotoSessionUiState

    data class BeforeStart(
        val startTimeLabel: String,
    ) : HomeTeamPhotoSessionUiState

    data object Closed : HomeTeamPhotoSessionUiState

    data object NotEligible : HomeTeamPhotoSessionUiState

    data object AwaitingPhoto : HomeTeamPhotoSessionUiState

    data object Reviewing : HomeTeamPhotoSessionUiState

    data class Pending(
        val uploadTimeLabel: String,
        val uploaderName: String?,
        val attendeeCount: Int,
    ) : HomeTeamPhotoSessionUiState

    data object ResultAvailable : HomeTeamPhotoSessionUiState

    data object Approved : HomeTeamPhotoSessionUiState
}
