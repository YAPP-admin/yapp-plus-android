package com.yapp.plus.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.yapp.plus.feature.login.LoginContent
import com.yapp.plus.feature.signup.AuthStatus
import com.yapp.plus.feature.signup.AuthStatusContent
import com.yapp.plus.feature.signup.SignUpContent
import com.yapp.plus.feature.signup.SignUpStep

@Composable
internal fun AuthDemoNavHost(
    uiState: AuthDemoUiState,
    onNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    BackHandler(
        enabled = currentEntry?.destination?.hasRoute<AuthDemoDestination.Login>() == true,
        onBack = onExit,
    )
    NavHost(
        navController = navController,
        startDestination = AuthDemoDestination.Login,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<AuthDemoDestination.Login> { entry ->
            LoginContent(
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onKakaoLogin = { navController.navigateFrom(entry, AuthDemoDestination.Name) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable<AuthDemoDestination.Name> { entry ->
            SignUpContent(
                step = SignUpStep.Name,
                value = uiState.name,
                onValueChange = onNameChange,
                isNextEnabled = true,
                onNext = { navController.navigateFrom(entry, AuthDemoDestination.Phone) },
                onBack = { navController.navigateBackFrom(entry, onExit) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable<AuthDemoDestination.Phone> { entry ->
            SignUpContent(
                step = SignUpStep.Phone,
                value = uiState.phoneNumber,
                onValueChange = onPhoneNumberChange,
                isNextEnabled = true,
                onNext = { navController.navigateFrom(entry, AuthDemoDestination.Pending) },
                onBack = { navController.navigateBackFrom(entry, onExit) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable<AuthDemoDestination.Pending> { entry ->
            AuthStatusContent(
                status = AuthStatus.Pending,
                onBack = { navController.navigateBackFrom(entry, onExit) },
                onAction = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

internal fun NavHostController.navigateFrom(
    entry: NavBackStackEntry,
    destination: AuthDemoDestination,
) {
    if (!canNavigateFrom(entry)) return
    navigate(destination) { launchSingleTop = true }
}

internal fun NavHostController.navigateBackFrom(
    entry: NavBackStackEntry,
    onExit: () -> Unit,
) {
    if (!canNavigateFrom(entry)) return
    if (entry.destination.hasRoute<AuthDemoDestination.Login>()) {
        onExit()
    } else {
        popBackStack()
    }
}

private fun NavHostController.canNavigateFrom(entry: NavBackStackEntry): Boolean =
    currentBackStackEntry == entry && entry.lifecycle.currentState == Lifecycle.State.RESUMED
