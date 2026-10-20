package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.components.navigation.YappBottomNavigation
import com.yapp.plus.core.designsystem.components.navigation.YappBottomNavigationItem

@Composable
internal fun MainTabNavigation(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    YappBottomNavigation(modifier = modifier) {
        for (tab in MainTab.entries) {
            YappBottomNavigationItem(
                label = stringResource(tab.labelResource),
                iconResource = tab.iconResource,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}
