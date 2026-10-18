package com.yapp.plus.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.yapp.plus.core.designsystem.component.navigation.YappBottomNavigationItem
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Attendance bottom navigation", widthDp = 393)
@Preview(name = "Compact attendance bottom navigation", widthDp = 320)
@Composable
private fun MainTabNavigationPreview(
    @PreviewParameter(MainTabPreviewParameterProvider::class) selectedTab: MainTab,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MainTabNavigation(selectedTab = selectedTab, onTabSelected = {})
    }
}

@Preview(name = "Attendance bottom navigation item", widthDp = 100, heightDp = 80)
@Composable
private fun MainTabNavigationItemPreview(
    @PreviewParameter(MainTabPreviewParameterProvider::class) tab: MainTab,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Row {
            YappBottomNavigationItem(
                label = stringResource(tab.labelResource),
                iconResource = tab.iconResource,
                isSelected = tab == MainTab.Home,
                onClick = {},
            )
        }
    }
}

@Preview(name = "Attendance tabs", widthDp = 393, heightDp = 790)
@Preview(name = "Tall attendance tabs", widthDp = 393, heightDp = 852)
@Composable
private fun MainTabScaffoldPreview(
    @PreviewParameter(MainTabPreviewParameterProvider::class) initialTab: MainTab,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        MainTabScaffold(initialTab = initialTab)
    }
}
