package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MyRoute(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MyViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyScreen(
        state = uiState,
        onRetry = viewModel::retry,
        onSettingsClick = onSettingsClick,
        modifier = modifier,
    )
}
