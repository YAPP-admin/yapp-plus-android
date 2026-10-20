package com.yapp.plus.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel

@Preview(name = "Home updated", widthDp = 393, heightDp = 692)
@Composable
fun UpdatedHomePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeScreen(HomePreviewData.state(), now = HomePreviewData.now)
    }
}

@Preview(name = "Home available", widthDp = 393, heightDp = 692)
@Composable
fun AvailableHomePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeScreen(
            HomePreviewData.state(AttendanceStatusUiModel.Available),
            now = HomePreviewData.now.withDayOfMonth(7),
            notices = HomePreviewData.notices(),
        )
    }
}

@Preview(name = "Home attended", widthDp = 393, heightDp = 692)
@Composable
fun AttendedHomePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeScreen(
            HomePreviewData.state(),
            hasAttended = true,
            now = HomePreviewData.now.withDayOfMonth(7),
            notices = HomePreviewData.notices(),
        )
    }
}

@Preview(name = "Notifications empty", widthDp = 393, heightDp = 692)
@Composable
fun EmptyNotificationsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { HomeNotificationsScreen(onBack = {}) }
}

@Preview(name = "Notifications", widthDp = 393, heightDp = 692)
@Composable
fun NotificationsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeNotificationsScreen(onBack = {}, notifications = HomePreviewData.notifications())
    }
}

@Preview(name = "Attendance complete", widthDp = 393, heightDp = 756)
@Composable
fun AttendanceCompletePreview() {
    YappTheme(
        darkTheme = false,
        dynamicColor = false,
    ) { HomeAttendanceCompleteScreen(onConfirm = {}) }
}

@Preview(name = "Native attendance animation", widthDp = 393, heightDp = 756)
@Composable
fun NativeAttendanceAnimationPreview() {
    CompositionLocalProvider(LocalInspectionMode provides false) {
        YappTheme(darkTheme = false, dynamicColor = false) {
            HomeAttendanceCompleteScreen(onConfirm = {})
        }
    }
}
