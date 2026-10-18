package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AttendanceHomeRoute(modifier: Modifier = Modifier) {
    val viewModel: AttendanceHomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AttendanceHomeScreen(
        state = state,
        modifier = modifier,
        onRetry = viewModel::retry,
    )
}
