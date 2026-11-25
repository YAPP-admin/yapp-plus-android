package com.yapp.plus.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.yapp.plus.R
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.domain.notification.SettlementNotificationTarget
import com.yapp.plus.feature.login.LoginRoute
import com.yapp.plus.feature.main.board.BoardRoute
import com.yapp.plus.feature.main.community.AfterpartyRoute
import com.yapp.plus.feature.main.community.MySettlementsRoute
import com.yapp.plus.feature.main.community.SettlementEditorRoute
import com.yapp.plus.feature.main.community.SettlementRoute
import com.yapp.plus.feature.main.home.attendance.HomeAttendanceCompleteScreen
import com.yapp.plus.feature.main.home.notification.HomeNotificationsRoute
import com.yapp.plus.feature.signup.SignUpRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val SETTLEMENT_CREATED_RECIPIENTS = "settlementCreatedRecipients"

@Composable
internal fun AppNavHost(
    onExit: () -> Unit,
    onGoogleCredentialReceived: suspend (GoogleLoginCredential) -> AuthResult = {
        AuthResult.Failed(com.yapp.plus.domain.auth.AuthFailure.Unavailable)
    },
    onLogout: (() -> Unit)? = null,
    isLoggingOut: Boolean = false,
    hasLogoutError: Boolean = false,
    onLogoutErrorDismissed: () -> Unit = {},
    onDeleteAccount: (() -> Unit)? = null,
    isDeletingAccount: Boolean = false,
    hasDeleteAccountError: Boolean = false,
    onDeleteAccountErrorDismissed: () -> Unit = {},
    registrationToken: String? = null,
    sessionAuthenticated: Boolean? = null,
    onRegistrationCompleted: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: AppNavigationDestination = AppNavigationDestination.Login,
    navController: NavHostController = rememberNavController(),
    pushPostId: Long? = null,
    onPushPostOpened: () -> Unit = {},
    pushSessionId: Long? = null,
    onPushSessionOpened: () -> Unit = {},
    pushSettlement: SettlementNotificationTarget? = null,
    onPushSettlementOpened: () -> Unit = {},
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(sessionAuthenticated, registrationToken, currentEntry) {
        val isAuthenticatedDestination =
            currentEntry?.destination?.isAuthenticatedDestination() == true
        when {
            sessionAuthenticated == true && !isAuthenticatedDestination -> {
                navController.navigate(AppNavigationDestination.Main) {
                    popUpTo<AppNavigationDestination.Login> { inclusive = true }
                    launchSingleTop = true
                }
            }

            sessionAuthenticated == false &&
                registrationToken.isNullOrBlank() &&
                currentEntry?.destination?.hasRoute<AppNavigationDestination.SignUp>() == true -> {
                navController.navigate(AppNavigationDestination.Login) {
                    popUpTo<AppNavigationDestination.SignUp> { inclusive = true }
                    launchSingleTop = true
                }
            }

            sessionAuthenticated == false && isAuthenticatedDestination -> {
                navController.navigate(AppNavigationDestination.Login) {
                    popUpTo<AppNavigationDestination.Main> { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
    LaunchedEffect(sessionAuthenticated, pushPostId, currentEntry) {
        val entry = currentEntry ?: return@LaunchedEffect
        if (sessionAuthenticated != true || pushPostId == null) return@LaunchedEffect
        if (!entry.destination.isAuthenticatedDestination()) return@LaunchedEffect
        val destination = AppNavigationDestination.NotificationPost(pushPostId)
        val isAlreadyOpen =
            entry.destination.hasRoute<AppNavigationDestination.NotificationPost>() &&
                entry.toRoute<AppNavigationDestination.NotificationPost>() == destination
        if (!isAlreadyOpen) {
            navController.navigate(destination) { launchSingleTop = true }
        }
        onPushPostOpened()
    }
    LaunchedEffect(sessionAuthenticated, pushSessionId, currentEntry) {
        val entry = currentEntry ?: return@LaunchedEffect
        if (sessionAuthenticated != true || pushSessionId == null) return@LaunchedEffect
        if (!entry.destination.isAuthenticatedDestination()) return@LaunchedEffect
        val destination = AppNavigationDestination.NotificationSchedule(pushSessionId)
        val isAlreadyOpen =
            entry.destination.hasRoute<AppNavigationDestination.NotificationSchedule>() &&
                entry.toRoute<AppNavigationDestination.NotificationSchedule>() == destination
        if (!isAlreadyOpen) {
            navController.navigate(destination) { launchSingleTop = true }
        }
        onPushSessionOpened()
    }
    LaunchedEffect(sessionAuthenticated, pushSettlement, currentEntry) {
        val entry = currentEntry ?: return@LaunchedEffect
        val target = pushSettlement ?: return@LaunchedEffect
        if (sessionAuthenticated != true || !entry.destination.isAuthenticatedDestination()) return@LaunchedEffect
        val destination = AppNavigationDestination.Settlement(target.afterpartyId, target.settlementId)
        val alreadyOpen =
            entry.destination.hasRoute<AppNavigationDestination.Settlement>() &&
                entry.toRoute<AppNavigationDestination.Settlement>() == destination
        if (!alreadyOpen) navController.navigate(destination) { launchSingleTop = true }
        onPushSettlementOpened()
    }
    BackHandler(
        enabled = currentEntry != null && navController.previousBackStackEntry == null,
        onBack = onExit,
    )
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
    ) {
        val mainContent: @Composable (NavBackStackEntry, Long?) -> Unit = { entry, sessionId ->
            MainTabScaffold(
                initialTab = if (sessionId == null) MainTab.Home else MainTab.Schedule,
                scheduleSessionId = sessionId,
                onAfterpartyClick = { navController.navigateFrom(entry, AppNavigationDestination.Afterparty(it)) },
                onSettlementsClick = { navController.navigateFrom(entry, AppNavigationDestination.MySettlements) },
                onNotificationsClick = {
                    navController.navigateFrom(entry, AppNavigationDestination.Notifications)
                },
                onAttendanceComplete = {
                    navController.navigateFrom(entry, AppNavigationDestination.AttendanceComplete)
                },
                onLogout = onLogout,
                isLoggingOut = isLoggingOut,
                hasLogoutError = hasLogoutError,
                onLogoutErrorDismissed = onLogoutErrorDismissed,
                onDeleteAccount = onDeleteAccount,
                isDeletingAccount = isDeletingAccount,
                hasDeleteAccountError = hasDeleteAccountError,
                onDeleteAccountErrorDismissed = onDeleteAccountErrorDismissed,
            )
        }
        composable<AppNavigationDestination.Login> { entry ->
            val uriHandler = LocalUriHandler.current
            val contactChannelUrl = stringResource(R.string.yapp_contact_channel_url)
            LoginRoute(
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onGoogleCredentialReceived = { credential ->
                    val result = onGoogleCredentialReceived(credential)
                    withContext(Dispatchers.Main.immediate) {
                        when (result) {
                            AuthResult.SignedIn -> {
                                navController.navigateToAuthenticatedMain(entry)
                                true
                            }

                            is AuthResult.SignupRequired -> {
                                navController.navigateFrom(entry, AppNavigationDestination.SignUp)
                                true
                            }

                            is AuthResult.Failed -> false
                        }
                    }
                },
                onReportError = { runCatching { uriHandler.openUri(contactChannelUrl) } },
            )
        }
        composable<AppNavigationDestination.SignUp> { entry ->
            SignUpRoute(
                onBack = { navController.navigateBackFromSignUp(entry, onExit) },
                registrationToken = registrationToken,
                onRegistrationCompleted = {
                    onRegistrationCompleted()
                    navController.navigateToAuthenticatedMain(entry)
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable<AppNavigationDestination.Main> { entry -> mainContent(entry, null) }
        composable<AppNavigationDestination.NotificationSchedule> { entry ->
            mainContent(entry, entry.toRoute<AppNavigationDestination.NotificationSchedule>().sessionId)
        }
        composable<AppNavigationDestination.AttendanceComplete> { entry ->
            HomeAttendanceCompleteScreen(
                onConfirm = { navController.navigateBackFrom(entry, onExit) },
            )
        }
        composable<AppNavigationDestination.Notifications> { entry ->
            HomeNotificationsRoute(
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onSettlementClick = { target ->
                    navController.navigateFrom(
                        entry,
                        AppNavigationDestination.Settlement(target.afterpartyId, target.settlementId),
                    )
                },
                onPostClick = {
                    navController.navigateFrom(entry, AppNavigationDestination.NotificationPost(it))
                },
                onSessionClick = {
                    navController.navigateFrom(entry, AppNavigationDestination.NotificationSchedule(it))
                },
            )
        }
        composable<AppNavigationDestination.Afterparty> { entry ->
            val id = entry.toRoute<AppNavigationDestination.Afterparty>().id
            val recipientCount by entry.savedStateHandle
                .getStateFlow<Int?>(SETTLEMENT_CREATED_RECIPIENTS, null)
                .collectAsStateWithLifecycle()
            AfterpartyRoute(
                id = id,
                settlementRecipientCount = recipientCount,
                onDismissSettlementNotice = {
                    entry.savedStateHandle[SETTLEMENT_CREATED_RECIPIENTS] = null
                },
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onSettlementClick = { navController.navigateFrom(entry, AppNavigationDestination.Settlement(id, it)) },
                onAddSettlement = { navController.navigateFrom(entry, AppNavigationDestination.SettlementEditor(id)) },
            )
        }
        composable<AppNavigationDestination.Settlement> { entry ->
            val target = entry.toRoute<AppNavigationDestination.Settlement>()
            SettlementRoute(
                afterpartyId = target.afterpartyId,
                settlementId = target.settlementId,
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onEdit = {
                    navController.navigateFrom(
                        entry,
                        AppNavigationDestination.SettlementEditor(target.afterpartyId, target.settlementId),
                    )
                },
            )
        }
        composable<AppNavigationDestination.SettlementEditor> { entry ->
            val target = entry.toRoute<AppNavigationDestination.SettlementEditor>()
            val onBack = { navController.navigateBackFrom(entry, onExit) }
            var isSaving by remember { mutableStateOf(false) }
            BackHandler(enabled = isSaving) {}
            SettlementEditorRoute(
                afterpartyId = target.afterpartyId,
                settlementId = target.settlementId,
                onBack = onBack,
                onSaved = { count: Int? ->
                    if (count != null) {
                        navController.previousBackStackEntry
                            ?.takeIf { it.destination.hasRoute<AppNavigationDestination.Afterparty>() }
                            ?.savedStateHandle
                            ?.set(SETTLEMENT_CREATED_RECIPIENTS, count)
                    }
                    onBack()
                },
                onSavingChanged = { isSaving = it },
            )
        }
        composable<AppNavigationDestination.MySettlements> { entry ->
            MySettlementsRoute(
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onClick = { afterpartyId, settlementId ->
                    navController.navigateFrom(entry, AppNavigationDestination.Settlement(afterpartyId, settlementId))
                },
            )
        }
        composable<AppNavigationDestination.NotificationPost> { entry ->
            BoardRoute(
                noticeId = entry.toRoute<AppNavigationDestination.NotificationPost>().postId,
                onNoticeClick = {},
                onBack = { navController.navigateBackFrom(entry, onExit) },
            )
        }
    }
}

private fun NavDestination.isAuthenticatedDestination(): Boolean =
    hasRoute<AppNavigationDestination.Main>() ||
        hasRoute<AppNavigationDestination.AttendanceComplete>() ||
        hasRoute<AppNavigationDestination.Notifications>() ||
        hasRoute<AppNavigationDestination.NotificationPost>() ||
        hasRoute<AppNavigationDestination.NotificationSchedule>() ||
        hasRoute<AppNavigationDestination.Afterparty>() ||
        hasRoute<AppNavigationDestination.Settlement>() ||
        hasRoute<AppNavigationDestination.SettlementEditor>() ||
        hasRoute<AppNavigationDestination.MySettlements>()

private fun NavHostController.navigateToAuthenticatedMain(entry: NavBackStackEntry) {
    if (!canNavigateFrom(entry)) return
    navigate(AppNavigationDestination.Main) {
        popUpTo<AppNavigationDestination.Login> { inclusive = true }
        launchSingleTop = true
    }
}

internal fun NavHostController.navigateFrom(
    entry: NavBackStackEntry,
    destination: AppNavigationDestination,
) {
    if (!canNavigateFrom(entry)) return
    navigate(destination) {
        launchSingleTop = true
        restoreState = destination == AppNavigationDestination.SignUp
    }
}

internal fun NavHostController.navigateBackFromSignUp(
    entry: NavBackStackEntry,
    onExit: () -> Unit,
) {
    if (!canNavigateFrom(entry)) return
    if (previousBackStackEntry == null) {
        onExit()
    } else {
        popBackStack<AppNavigationDestination.SignUp>(inclusive = true, saveState = true)
    }
}

internal fun NavHostController.navigateBackFrom(
    entry: NavBackStackEntry,
    onExit: () -> Unit,
) {
    if (!canNavigateFrom(entry)) return
    if (previousBackStackEntry == null) {
        onExit()
    } else {
        popBackStack()
    }
}

private fun NavHostController.canNavigateFrom(entry: NavBackStackEntry): Boolean =
    currentBackStackEntry == entry && entry.lifecycle.currentState == Lifecycle.State.RESUMED
