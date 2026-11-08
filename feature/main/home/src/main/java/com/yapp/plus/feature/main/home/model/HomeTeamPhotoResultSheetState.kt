package com.yapp.plus.feature.main.home.model

internal sealed interface HomeTeamPhotoResultSheetState {
    data object Approved : HomeTeamPhotoResultSheetState

    data class Rejected(
        val retryDeadlineLabel: String?,
        val reason: String?,
        val canRetry: Boolean,
    ) : HomeTeamPhotoResultSheetState

    data class ConfirmedAbsent(
        val reason: String?,
    ) : HomeTeamPhotoResultSheetState

    data object UploadFailed : HomeTeamPhotoResultSheetState
}
