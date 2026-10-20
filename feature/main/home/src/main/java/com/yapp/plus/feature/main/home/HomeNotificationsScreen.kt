package com.yapp.plus.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.components.HomeEmptyState
import com.yapp.plus.feature.main.home.components.HomeTopBar
import com.yapp.plus.feature.main.home.model.HomeNotificationUiModel

@Composable
fun HomeNotificationsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    notifications: List<HomeNotificationUiModel> = emptyList(),
) {
    Column(modifier.fillMaxSize().background(YappHomeColor.surface)) {
        HomeTopBar(stringResource(R.string.attendance_home_notifications), onBack)
        if (notifications.isEmpty()) {
            HomeEmptyState(
                message = stringResource(R.string.home_notifications_empty),
                modifier = Modifier.fillMaxSize(),
                graphicWidth = 90.dp,
                graphicHeight = 74.dp,
                spacing = 20.dp,
                textStyle = YappHomeTypography.bodyLarge,
                graphicAlpha = 1f,
            )
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(notifications, key = { it.id }) { notification ->
                    HomeNotificationItem(notification)
                }
            }
        }
    }
}

@Composable
internal fun HomeNotificationItem(notification: HomeNotificationUiModel) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        YappText(notification.message, YappHomeTypography.bodyLarge, YappHomeColor.text)
        YappText(notification.dateLabel, YappHomeTypography.label, YappHomeColor.subtle)
    }
    HorizontalDivider(color = YappHomeColor.divider)
}
