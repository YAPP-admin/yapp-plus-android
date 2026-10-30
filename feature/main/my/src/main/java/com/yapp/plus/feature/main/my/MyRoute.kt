package com.yapp.plus.feature.main.my

import androidx.compose.runtime.Composable
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
) {
    val viewModel: MyViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(isHistoryVisible) {
        viewModel.fetchProfile()
        viewModel.fetchScore()
        if (isHistoryVisible) viewModel.fetchHistory()
    }
    if (isHistoryVisible) {
        MyAttendanceHistoryScreen(
            state = state.attendanceHistory,
            absentCount = (state.score as? MyAttendanceScoreState.Content)?.score?.absentCount,
            onBack = onBack,
            onRetry = viewModel::fetchHistory,
            onLoadMore = viewModel::fetchNextPage,
            modifier = modifier,
        )
    } else {
        MyScreen(
            state = state,
            onRetryScore = viewModel::fetchScore,
            onAttendanceHistoryClick = onAttendanceHistoryClick,
            onSettingsClick = onSettingsClick,
            onContactClick = onContactClick,
            modifier = modifier,
        )
    }
}
