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
import com.yapp.plus.BuildConfig
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.board.BoardRoute
import com.yapp.plus.feature.main.home.HomeRoute
import com.yapp.plus.feature.main.my.MyRoute
import com.yapp.plus.feature.main.schedule.ScheduleRoute
import com.yapp.plus.feature.setting.SettingsScreen

@Composable
internal fun MainTabScaffold(
    modifier: Modifier = Modifier,
    initialTab: MainTab = MainTab.Home,
    onAttendanceComplete: () -> Unit = {},
    onLogout: () -> Unit = {},
    isLoggingOut: Boolean = false,
    hasLogoutError: Boolean = false,
    onLogoutErrorDismissed: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    isDeletingAccount: Boolean = false,
    hasDeleteAccountError: Boolean = false,
    onDeleteAccountErrorDismissed: () -> Unit = {},
) {
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    var isSettingsVisible by rememberSaveable { mutableStateOf(false) }
    var selectedNoticeId by rememberSaveable { mutableStateOf<Long?>(null) }
    BackHandler(enabled = selectedTab == MainTab.Board && selectedNoticeId != null) {
        selectedNoticeId = null
    }
    BackHandler(enabled = selectedTab == MainTab.My && isSettingsVisible) {
        isSettingsVisible = false
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappMainColor.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!isSettingsVisible) {
                MainTabNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        isSettingsVisible = false
                        selectedNoticeId = null
                    },
                )
            }
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
                MainTab.Home -> {
                    HomeRoute(
                        onSessionsClick = { selectedTab = MainTab.Schedule },
                        onNoticeClick = {
                            selectedNoticeId = it
                            selectedTab = MainTab.Board
                        },
                        onAttendanceComplete = onAttendanceComplete,
                    )
                }
                MainTab.Schedule -> ScheduleRoute()
                MainTab.Board ->
                    BoardRoute(
                        noticeId = selectedNoticeId,
                        onNoticeClick = { selectedNoticeId = it },
                        onBack = { selectedNoticeId = null },
                    )
                MainTab.My ->
                    if (isSettingsVisible) {
                        SettingsScreen(
                            appVersion = BuildConfig.VERSION_NAME,
                            onBack = { isSettingsVisible = false },
                            onLogout = onLogout,
                            isLoggingOut = isLoggingOut,
                            hasLogoutError = hasLogoutError,
                            onLogoutErrorDismissed = onLogoutErrorDismissed,
                            onDeleteAccount = onDeleteAccount,
                            isDeletingAccount = isDeletingAccount,
                            hasDeleteAccountError = hasDeleteAccountError,
                            onDeleteAccountErrorDismissed = onDeleteAccountErrorDismissed,
                        )
                    } else {
                        MyRoute(onSettingsClick = { isSettingsVisible = true })
                    }
            }
        }
    }
}
