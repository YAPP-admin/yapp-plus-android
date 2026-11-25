package com.yapp.plus.feature.main.home.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.notification.SettlementNotificationTarget

@Composable
fun HomeNotificationsRoute(
    onBack: () -> Unit,
    onPostClick: (Long) -> Unit,
    onSessionClick: (Long) -> Unit = {},
    onSettlementClick: (SettlementNotificationTarget) -> Unit = {},
) {
    val viewModel: HomeNotificationsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeNotificationsScreen(
        state = state,
        onBack = onBack,
        onPostClick = onPostClick,
        onSessionClick = onSessionClick,
        onSettlementClick = onSettlementClick,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
    )
}
