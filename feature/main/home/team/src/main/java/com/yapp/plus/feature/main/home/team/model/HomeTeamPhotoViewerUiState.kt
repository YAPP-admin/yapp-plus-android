package com.yapp.plus.feature.main.home.team.model

import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage

sealed interface HomeTeamPhotoViewerUiState {
    data object Loading : HomeTeamPhotoViewerUiState

    data object Unavailable : HomeTeamPhotoViewerUiState

    data object Error : HomeTeamPhotoViewerUiState

    data class Content(
        val image: TeamAttendancePhotoImage,
    ) : HomeTeamPhotoViewerUiState
}
