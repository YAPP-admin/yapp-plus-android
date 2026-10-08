package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.component.navigation.YappBottomNavigation
import com.yapp.plus.core.designsystem.component.navigation.YappBottomNavigationItem

@Composable
fun AttendanceBottomNavigation(
    selectedTab: AttendanceTab,
    onTabSelected: (AttendanceTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    YappBottomNavigation(modifier = modifier) {
        for (tab in AttendanceTab.entries) {
            YappBottomNavigationItem(
                label = stringResource(tab.labelResource),
                iconResource = tab.iconResource,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}
