package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AttendanceScheduleRoute(modifier: Modifier = Modifier) {
    val viewModel: AttendanceScheduleViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AttendanceScheduleScreen(
        state = state,
        modifier = modifier,
        onRetry = viewModel::retry,
    )
}
