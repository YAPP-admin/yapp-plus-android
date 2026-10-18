package com.yapp.plus.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.attendance.AttendanceBoardScreen
import com.yapp.plus.feature.attendance.AttendanceHomeRoute
import com.yapp.plus.feature.attendance.AttendanceMyScreen
import com.yapp.plus.feature.attendance.AttendanceScheduleRoute

@Composable
internal fun MainTabScaffold(
    modifier: Modifier = Modifier,
    initialTab: MainTab = MainTab.Home,
) {
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappMainColor.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainTabNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { contentPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
        ) {
            when (selectedTab) {
                MainTab.Home -> AttendanceHomeRoute()
                MainTab.Schedule -> AttendanceScheduleRoute()
                MainTab.Board -> AttendanceBoardScreen()
                MainTab.My -> AttendanceMyScreen()
            }
        }
    }
}
