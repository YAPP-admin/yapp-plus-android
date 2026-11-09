package com.yapp.plus.feature.main.home.model

internal sealed interface HomeNotificationsUiState {
    data object Loading : HomeNotificationsUiState

    data object Error : HomeNotificationsUiState

    data class Content(
        val items: List<HomeNotificationUiModel> = emptyList(),
        val nextCursor: String? = null,
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        val hasRefreshError: Boolean = false,
        val hasLoadMoreError: Boolean = false,
    ) : HomeNotificationsUiState
}
