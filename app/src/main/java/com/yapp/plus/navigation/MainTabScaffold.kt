package com.yapp.plus.navigation

import androidx.activity.compose.BackHandler
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
import com.yapp.plus.feature.main.board.BoardScreen
import com.yapp.plus.feature.main.home.HomeNotificationsScreen
import com.yapp.plus.feature.main.home.HomeRoute
import com.yapp.plus.feature.main.my.MyScreen
import com.yapp.plus.feature.main.schedule.ScheduleRoute

@Composable
internal fun MainTabScaffold(
    modifier: Modifier = Modifier,
    initialTab: MainTab = MainTab.Home,
    onAttendanceComplete: () -> Unit = {},
) {
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    var areNotificationsVisible by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = selectedTab == MainTab.Home && areNotificationsVisible) {
        areNotificationsVisible = false
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappMainColor.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainTabNavigation(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    areNotificationsVisible = false
                },
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
                MainTab.Home ->
                    if (areNotificationsVisible) {
                        HomeNotificationsScreen(onBack = { areNotificationsVisible = false })
                    } else {
                        HomeRoute(
                            onNotificationsClick = { areNotificationsVisible = true },
                            onSessionsClick = { selectedTab = MainTab.Schedule },
                            onScoreClick = { selectedTab = MainTab.My },
                            onNoticeClick = { selectedTab = MainTab.Board },
                            onAttendanceComplete = onAttendanceComplete,
                        )
                    }
                MainTab.Schedule -> ScheduleRoute()
                MainTab.Board -> BoardScreen()
                MainTab.My -> MyScreen()
            }
        }
    }
}
