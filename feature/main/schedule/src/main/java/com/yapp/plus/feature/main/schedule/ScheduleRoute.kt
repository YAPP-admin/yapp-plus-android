package com.yapp.plus.feature.main.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState

@Composable
fun ScheduleRoute(
    modifier: Modifier = Modifier,
    targetSessionId: Long? = null,
) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        if (viewModel.uiState.value == ScheduleUiState.Error) viewModel.retry()
    }

    ScheduleScreen(
        state = state,
        modifier = modifier,
        targetSessionId = targetSessionId,
    )
}
