package com.yapp.plus.feature.main.home.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.feature.main.home.notification.model.HomeNotificationUiModel

internal object HomeNotificationPreviewData {
    @Composable
    fun notifications(): List<HomeNotificationUiModel> =
        listOf(
            HomeNotificationUiModel(
                1L,
                stringResource(R.string.home_preview_notification_session),
                stringResource(R.string.home_preview_notification_date),
            ),
            HomeNotificationUiModel(
                2L,
                stringResource(R.string.home_preview_notification_place),
                stringResource(R.string.home_preview_notification_date),
            ),
            HomeNotificationUiModel(
                3L,
                stringResource(R.string.home_preview_notification_notice),
                stringResource(R.string.home_preview_notification_date),
            ),
        )
}
