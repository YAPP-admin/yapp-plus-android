package com.yapp.plus.feature.main.home.model

sealed interface HomeNoticeUiState {
    data object Unavailable : HomeNoticeUiState

    data object Loading : HomeNoticeUiState

    data object Error : HomeNoticeUiState

    data object Empty : HomeNoticeUiState

    data class Content(
        val notices: List<HomeNoticeUiModel>,
    ) : HomeNoticeUiState
}
