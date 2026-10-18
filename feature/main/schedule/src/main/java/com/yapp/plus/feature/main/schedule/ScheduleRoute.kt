package com.yapp.plus.feature.main.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScheduleRoute(modifier: Modifier = Modifier) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ScheduleScreen(
        state = state,
        modifier = modifier,
        onRetry = viewModel::retry,
    )
}
