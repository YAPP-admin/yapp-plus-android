package com.yapp.plus.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.domain.notification.SettlementNotificationTarget
import com.yapp.plus.feature.main.home.components.HomeEmptyState
import com.yapp.plus.feature.main.home.components.HomeTopBar
import com.yapp.plus.feature.main.home.model.HomeNotificationUiModel
import com.yapp.plus.feature.main.home.model.HomeNotificationsUiState

@Composable
internal fun HomeNotificationsScreen(
    state: HomeNotificationsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onPostClick: (Long) -> Unit = {},
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onSessionClick: (Long) -> Unit = {},
    onSettlementClick: (SettlementNotificationTarget) -> Unit = {},
) {
    Column(modifier.fillMaxSize().background(YappHomeColor.surface)) {
        HomeTopBar(stringResource(R.string.attendance_home_notifications), onBack)
        when (state) {
            HomeNotificationsUiState.Loading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = YappHomeColor.brand)
                }
            HomeNotificationsUiState.Error ->
                NotificationList(
                    state = HomeNotificationsUiState.Content(hasRefreshError = true),
                    onPostClick = onPostClick,
                    onSettlementClick = onSettlementClick,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    onSessionClick = onSessionClick,
                )
            is HomeNotificationsUiState.Content ->
                NotificationList(state, onPostClick, onRefresh, onLoadMore, onSessionClick, onSettlementClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationList(
    state: HomeNotificationsUiState.Content,
    onPostClick: (Long) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onSessionClick: (Long) -> Unit = {},
    onSettlementClick: (SettlementNotificationTarget) -> Unit = {},
) {
    PullToRefreshBox(isRefreshing = state.isRefreshing, onRefresh = onRefresh) {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            if (state.hasRefreshError) {
                item { NotificationListError() }
            }
            if (state.items.isEmpty() && state.nextCursor == null && !state.hasRefreshError) {
                item {
                    HomeEmptyState(
                        message = stringResource(R.string.home_notifications_empty),
                        modifier = Modifier.fillParentMaxSize(),
                        graphicWidth = 90.dp,
                        graphicHeight = 74.dp,
                        spacing = 20.dp,
                        textStyle = YappHomeTypography.bodyLarge,
                        graphicAlpha = 1f,
                    )
                }
            }
            items(state.items, key = { it.id }) { notification ->
                val onClick =
                    notification.settlement?.let { target -> { onSettlementClick(target) } }
                        ?: notification.sessionId?.let { id -> { onSessionClick(id) } }
                        ?: notification.postId?.let { id -> { onPostClick(id) } }
                HomeNotificationItem(notification = notification, onClick = onClick)
            }
            if (state.nextCursor != null) {
                item { NotificationPagination(state, onLoadMore) }
            }
        }
    }
}

@Composable
internal fun NotificationPagination(
    state: HomeNotificationsUiState.Content,
    onLoadMore: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoadingMore -> CircularProgressIndicator(color = YappHomeColor.brand)
            else ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (state.hasLoadMoreError) NotificationListError()
                    YappButton(
                        text = stringResource(R.string.home_notifications_load_more),
                        onClick = onLoadMore,
                        enabled = !state.isRefreshing,
                        variant = YappButtonVariant.SolidNeutral,
                        size = YappButtonSize.HomeSecondary,
                    )
                }
        }
    }
}

@Composable
internal fun NotificationListError() {
    YappText(
        stringResource(R.string.home_notifications_load_error),
        YappHomeTypography.body,
        YappHomeColor.muted,
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
internal fun HomeNotificationItem(
    notification: HomeNotificationUiModel,
    onClick: (() -> Unit)? = null,
) {
    val interaction =
        if (onClick == null) {
            Modifier
        } else {
            Modifier.noRippleClickable(
                onClickLabel =
                    stringResource(
                        when {
                            notification.settlement != null -> R.string.home_notifications_open_settlement
                            notification.sessionId != null -> R.string.home_notifications_open_schedule
                            else -> R.string.home_notifications_open_post
                        },
                    ),
                role = Role.Button,
                onClick = onClick,
            )
        }
    Column(
        Modifier
            .fillMaxWidth()
            .then(interaction)
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        YappText(notification.message, YappHomeTypography.bodyLarge, YappHomeColor.text)
        YappText(notification.dateLabel, YappHomeTypography.label, YappHomeColor.subtle)
    }
    HorizontalDivider(color = YappHomeColor.divider)
}
