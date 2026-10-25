package com.yapp.plus.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.feature.login.LoginRoute
import com.yapp.plus.feature.main.home.HomeAttendanceCompleteScreen
import com.yapp.plus.feature.signup.SignUpRoute

@Composable
internal fun AppNavHost(
    onExit: () -> Unit,
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: AppNavigationDestination = AppNavigationDestination.Login,
    navController: NavHostController = rememberNavController(),
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    BackHandler(
        enabled = currentEntry != null && navController.previousBackStackEntry == null,
        onBack = onExit,
    )
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<AppNavigationDestination.Login> { entry ->
            LoginRoute(
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onGoogleCredentialReceived = onGoogleCredentialReceived,
            )
        }
        composable<AppNavigationDestination.SignUp> { entry ->
            SignUpRoute(
                onBack = { navController.navigateBackFromSignUp(entry, onExit) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable<AppNavigationDestination.Main> { entry ->
            MainTabScaffold(
                onAttendanceComplete = {
                    navController.navigateFrom(entry, AppNavigationDestination.AttendanceComplete)
                },
            )
        }
        composable<AppNavigationDestination.AttendanceComplete> { entry ->
            HomeAttendanceCompleteScreen(
                onConfirm = { navController.navigateBackFrom(entry, onExit) },
            )
        }
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
