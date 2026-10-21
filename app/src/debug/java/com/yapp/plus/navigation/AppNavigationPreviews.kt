package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(name = "Session restoration", widthDp = 393, heightDp = 756)
@Composable
private fun AppStartContentPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AppStartContent(uiState = AppAuthUiState.Restoring) {}
    }
}

@Preview(name = "Sign-up app", widthDp = 393, heightDp = 756)
@Composable
private fun AppNavigationRoutePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AppNavigationRoute(onExit = {})
    }
}

@Preview(name = "Main app", widthDp = 393, heightDp = 756)
@Composable
private fun MainRoutePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AppNavigationRoute(
            onExit = {},
            startDestination = AppNavigationDestination.Main,
        )
    }
}

@Preview(name = "App navigation", widthDp = 393, heightDp = 756)
@Composable
private fun AppNavHostPreview() {
    val navController = rememberNavController()
    YappTheme(darkTheme = false, dynamicColor = false) {
        AppNavHost(
            onExit = {},
            navController = navController,
        )
    }
    LaunchedEffect(navController) { navController.navigate(AppNavigationDestination.Name) }
}
