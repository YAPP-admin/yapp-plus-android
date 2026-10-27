package com.yapp.plus.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.feature.login.LoginRoute
import com.yapp.plus.feature.main.home.HomeAttendanceCompleteScreen
import com.yapp.plus.feature.signup.SignUpRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun AppNavHost(
    onExit: () -> Unit,
    onGoogleCredentialReceived: suspend (GoogleLoginCredential) -> AuthResult = {
        AuthResult.Failed(com.yapp.plus.domain.auth.AuthFailure.Unavailable)
    },
    registrationToken: String? = null,
    sessionAuthenticated: Boolean? = null,
    onRegistrationCompleted: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: AppNavigationDestination = AppNavigationDestination.Login,
    navController: NavHostController = rememberNavController(),
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(sessionAuthenticated, registrationToken, currentEntry) {
        when {
            sessionAuthenticated == true &&
                currentEntry?.destination?.hasRoute<AppNavigationDestination.Main>() != true &&
                currentEntry?.destination?.hasRoute<AppNavigationDestination.AttendanceComplete>() != true -> {
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

            sessionAuthenticated == false &&
                (
                    currentEntry?.destination?.hasRoute<AppNavigationDestination.Main>() == true ||
                        currentEntry?.destination?.hasRoute<AppNavigationDestination.AttendanceComplete>() == true
                ) -> {
                navController.navigate(AppNavigationDestination.Login) {
                    popUpTo<AppNavigationDestination.Main> { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
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
        composable<AppNavigationDestination.Login> { entry ->
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
