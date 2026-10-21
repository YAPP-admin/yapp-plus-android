package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.feature.main.home.components.HomeSheetContent
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeSheet

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onSessionsClick: () -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    onAttendanceComplete: () -> Unit = {},
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val interactionViewModel: HomeInteractionViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val interaction by interactionViewModel.uiState.collectAsStateWithLifecycle()
    val session = (state as? AttendanceUiState.Content)?.value
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(interaction.isAttendanceComplete, lifecycleOwner) {
        if (interaction.isAttendanceComplete) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                onAttendanceComplete()
                interactionViewModel.acknowledgeAttendanceComplete()
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        HomeScreen(
            state = state,
            modifier = Modifier.fillMaxSize(),
            onRetry = viewModel::retry,
            noticeState = notices,
            onNoticeRetry = viewModel::retryNotices,
            onAttendanceClick = interactionViewModel::openAttendanceCode,
            onSessionsClick = onSessionsClick,
            onNoticeClick = onNoticeClick,
            hasAttended = session != null && interaction.attendedSessionId == session.sessionId,
        )
    }
    if (interaction.sheet != HomeSheet.None) {
        YappBottomSheet(
            onDismissRequest = interactionViewModel::dismissSheet,
            onConfirm = {},
            onDismiss = {},
            title = null,
            body = null,
            confirmText = null,
            dismissText = null,
            showHandle = true,
            showActions = false,
            variant = YappBottomSheetVariant.Home,
        ) {
            HomeSheetContent(
                state = interaction,
                onCodeChange = interactionViewModel::updateCode,
                onConfirm = {
                    session?.let {
                        interactionViewModel.verifyAttendance(it.sessionId)
                    }
                },
            )
        }
    }
}
