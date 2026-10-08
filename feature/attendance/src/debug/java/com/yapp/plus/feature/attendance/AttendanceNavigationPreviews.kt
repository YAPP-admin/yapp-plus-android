package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Attendance bottom navigation", widthDp = 393)
@Preview(name = "Compact attendance bottom navigation", widthDp = 320)
@Composable
private fun AttendanceBottomNavigationPreview(
    @PreviewParameter(AttendanceTabPreviewParameterProvider::class) selectedTab: AttendanceTab,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceBottomNavigation(selectedTab = selectedTab, onTabSelected = {})
    }
}

@Preview(name = "Attendance tabs", widthDp = 393, heightDp = 790)
@Composable
private fun AttendanceContentPreview(
    @PreviewParameter(AttendanceTabPreviewParameterProvider::class) initialTab: AttendanceTab,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceContent(initialTab = initialTab)
    }
}
