package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.feature.main.home.components.HomeReportConfirmation
import com.yapp.plus.feature.main.home.components.HomeReportSheet
import com.yapp.plus.feature.main.home.components.HomeSheetContent
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeSheet

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
    onSessionsClick: () -> Unit = {},
    onScoreClick: () -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    onAttendanceComplete: () -> Unit = {},
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val noticeViewModel: HomeNoticeViewModel = hiltViewModel()
    val notices by noticeViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(noticeViewModel) { noticeViewModel.fetchNotices() }
    val interactionViewModel: HomeInteractionViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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

    LaunchedEffect(interaction.errorResource) {
        if (interaction.errorResource == R.string.home_report_session_changed) viewModel.retry()
    }
    Box(modifier.fillMaxSize()) {
        HomeScreen(
            state = state,
            modifier = Modifier.fillMaxSize(),
            onRetry = viewModel::retry,
            noticeState = notices,
            onNoticeRetry = noticeViewModel::fetchNotices,
            onNotificationsClick = onNotificationsClick,
            onAttendanceClick = interactionViewModel::openAttendanceCode,
            onSessionsClick = onSessionsClick,
            onScoreClick = onScoreClick,
            onReportClick = interactionViewModel::openReport,
            onNoticeClick = onNoticeClick,
            hasAttended = session != null && interaction.attendedSessionId == session.sessionId,
        )
        if (interaction.isReportComplete) {
            HomeReportConfirmation(
                interactionViewModel::acknowledgeReportComplete,
                Modifier.align(Alignment.BottomCenter).padding(16.dp),
            )
        }
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
            isDismissible = !interaction.isSubmitting,
        ) {
            if (interaction.sheet != HomeSheet.AttendanceCode) {
                HomeReportSheet(
                    interaction,
                    interactionViewModel::selectReportType,
                    interactionViewModel::updateReportReason,
                    interactionViewModel::confirmReport,
                    canSubmit = state !is AttendanceUiState.Loading,
                )
            } else {
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
}
