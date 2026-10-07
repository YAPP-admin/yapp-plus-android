package com.yapp.plus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.rememberNavController
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.preview.TextInputPreviewParameterProvider

@Preview(name = "Auth demo app", widthDp = 393, heightDp = 756)
@Composable
private fun AuthDemoRoutePreview() {
    val viewModel = remember { AuthDemoViewModel(SavedStateHandle()) }
    YappTheme(darkTheme = false, dynamicColor = false) {
        AuthDemoRoute(onExit = {}, viewModel = viewModel)
    }
}

@Preview(name = "Auth demo navigation", widthDp = 393, heightDp = 756)
@Composable
private fun AuthDemoNavHostPreview() {
    val navController = rememberNavController()
    val fixture = TextInputPreviewParameterProvider().values.first { it.value.isNotEmpty() }
    YappTheme(darkTheme = false, dynamicColor = false) {
        AuthDemoNavHost(
            uiState = AuthDemoUiState(name = fixture.value),
            onNameChange = {},
            onPhoneNumberChange = {},
            onExit = {},
            navController = navController,
        )
    }
    LaunchedEffect(navController) { navController.navigate(AuthDemoDestination.Name) }
}
