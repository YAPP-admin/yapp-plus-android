package com.yapp.plus.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.feature.login.GoogleLoginCredential

@Composable
internal fun AuthDemoRoute(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: AuthDemoDestination = AuthDemoDestination.Login,
    viewModel: AuthDemoViewModel = viewModel(),
    onGoogleCredentialReceived: (GoogleLoginCredential) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = YappAuthColor.background,
    ) { innerPadding ->
        AuthDemoNavHost(
            uiState = uiState,
            onNameChange = viewModel::updateName,
            onPhoneNumberChange = viewModel::updatePhoneNumber,
            onExit = onExit,
            startDestination = startDestination,
            onGoogleCredentialReceived = onGoogleCredentialReceived,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
        )
    }
}
