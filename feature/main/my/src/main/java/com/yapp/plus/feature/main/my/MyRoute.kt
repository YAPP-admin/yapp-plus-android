package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.feature.main.my.model.MyAttendanceScoreState

@Composable
fun MyRoute(
    onSettingsClick: () -> Unit,
    onAttendanceHistoryClick: () -> Unit,
    onBack: () -> Unit,
    isHistoryVisible: Boolean,
    modifier: Modifier = Modifier,
    onContactClick: () -> Unit = {},
    onSettlementsClick: (() -> Unit)? = null,
) {
    val viewModel: MyViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, isHistoryVisible) {
        viewModel.loadInitialData(loadHistory = isHistoryVisible)
    }
    DisposableEffect(viewModel) {
        onDispose(viewModel::cancelInitialLoad)
    }
    if (isHistoryVisible) {
        MyAttendanceHistoryScreen(
            state = state.attendanceHistory,
            absentCount = (state.score as? MyAttendanceScoreState.Content)?.score?.absentCount,
            onBack = onBack,
            onLoadMore = viewModel::fetchNextPage,
            modifier = modifier,
            isScreenLoading = state.isLoading,
        )
    } else {
        MyScreen(
            state = state,
            onAttendanceHistoryClick = onAttendanceHistoryClick,
            onSettingsClick = onSettingsClick,
            onContactClick = onContactClick,
            onSettlementsClick = onSettlementsClick,
            modifier = modifier,
        )
    }
}
