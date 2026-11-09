package com.yapp.plus.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.model.HomeNotificationsUiState

@Preview(name = "알림 항목", showBackground = true)
@Composable
fun HomeNotificationItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeNotificationItem(HomePreviewData.notifications().first(), onClick = {})
    }
}

@Preview(name = "다음 페이지 재시도", showBackground = true)
@Composable
fun HomeNotificationPaginationPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        NotificationPagination(
            HomeNotificationsUiState.Content(nextCursor = "next", hasLoadMoreError = true),
            onLoadMore = {},
        )
    }
}

@Preview(name = "알림 목록 오류", widthDp = 393, heightDp = 692)
@Composable
fun HomeNotificationErrorPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeNotificationsScreen(state = HomeNotificationsUiState.Error, onBack = {})
    }
}
