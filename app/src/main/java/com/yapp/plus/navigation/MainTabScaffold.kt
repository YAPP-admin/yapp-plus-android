package com.yapp.plus.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.yapp.plus.R
import com.yapp.plus.core.designsystem.components.toast.YappToastManager
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.board.BoardRoute
import com.yapp.plus.feature.main.community.AfterpartyBanner
import com.yapp.plus.feature.main.community.CommunityRoute
import com.yapp.plus.feature.main.home.HomeRoute
import com.yapp.plus.feature.main.my.MyRoute
import com.yapp.plus.feature.main.schedule.ScheduleRoute
import com.yapp.plus.feature.setting.SettingsScreen
import com.yapp.plus.notification.NotificationSettingsRoute

@Composable
internal fun MainTabScaffold(
    modifier: Modifier = Modifier,
    initialTab: MainTab = MainTab.Home,
    onHomeEntered: () -> Unit = {},
    onAttendanceComplete: () -> Unit = {},
    onNotificationsClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    isLoggingOut: Boolean = false,
    hasLogoutError: Boolean = false,
    onLogoutErrorDismissed: () -> Unit = {},
    onDeleteAccount: (() -> Unit)? = null,
    isDeletingAccount: Boolean = false,
    hasDeleteAccountError: Boolean = false,
    onDeleteAccountErrorDismissed: () -> Unit = {},
    scheduleSessionId: Long? = null,
    onAfterpartyClick: ((Long) -> Unit)? = null,
    onSettlementsClick: (() -> Unit)? = null,
) {
    val toastManager = remember { YappToastManager() }
    val uriHandler = LocalUriHandler.current
    val contactChannelUrl = stringResource(R.string.yapp_contact_channel_url)
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    var isSettingsVisible by rememberSaveable { mutableStateOf(false) }
    var isMyHistoryVisible by rememberSaveable { mutableStateOf(false) }
    var selectedNoticeId by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(selectedTab) {
        if (selectedTab == MainTab.Home) onHomeEntered()
    }
    BackHandler(enabled = selectedTab == MainTab.Board && selectedNoticeId != null) {
        selectedNoticeId = null
    }
    BackHandler(enabled = selectedTab == MainTab.My && isSettingsVisible) {
        isSettingsVisible = false
    }
    BackHandler(enabled = selectedTab == MainTab.My && isMyHistoryVisible) {
        isMyHistoryVisible = false
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappMainColor.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!isSettingsVisible && !isMyHistoryVisible && selectedNoticeId == null) {
                MainTabNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        isSettingsVisible = false
                        isMyHistoryVisible = false
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
                MainTab.Home ->
                    HomeRoute(
                        toastManager = toastManager,
                        onNotificationsClick = onNotificationsClick,
                        afterpartyBanner = onAfterpartyClick?.let { navigate -> { AfterpartyBanner(navigate) } },
                        onSessionsClick = { selectedTab = MainTab.Schedule },
                        onNoticeClick = {
                            selectedNoticeId = it
                            selectedTab = MainTab.Board
                        },
                        onAttendanceComplete = onAttendanceComplete,
                        onContactStaff = { runCatching { uriHandler.openUri(contactChannelUrl) } },
                    )
                MainTab.Schedule -> ScheduleRoute(targetSessionId = scheduleSessionId)
                MainTab.Board ->
                    BoardRoute(
                        toastManager = toastManager,
                        noticeId = selectedNoticeId,
                        onNoticeClick = { selectedNoticeId = it },
                        onBack = { selectedNoticeId = null },
                    )
                MainTab.Community -> CommunityRoute { onAfterpartyClick?.invoke(it) }
                MainTab.My ->
                    if (isSettingsVisible) {
                        SettingsScreen(
                            onBack = { isSettingsVisible = false },
                            onLogout = onLogout,
                            isLoggingOut = isLoggingOut,
                            hasLogoutError = hasLogoutError,
                            onLogoutErrorDismissed = onLogoutErrorDismissed,
                            onDeleteAccount = onDeleteAccount,
                            isDeletingAccount = isDeletingAccount,
                            hasDeleteAccountError = hasDeleteAccountError,
                            onDeleteAccountErrorDismissed = onDeleteAccountErrorDismissed,
                            notificationSettings = { NotificationSettingsRoute() },
                        )
                    } else {
                        MyRoute(
                            onSettingsClick = { isSettingsVisible = true },
                            onSettlementsClick = onSettlementsClick,
                            onAttendanceHistoryClick = { isMyHistoryVisible = true },
                            onBack = { isMyHistoryVisible = false },
                            isHistoryVisible = isMyHistoryVisible,
                            onContactClick = { runCatching { uriHandler.openUri(contactChannelUrl) } },
                        )
                    }
            }
        }
    }
}
