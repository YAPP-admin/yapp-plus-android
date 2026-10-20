package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.components.toast.YappToast
import com.yapp.plus.core.designsystem.components.toast.YappToastVariant
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

    Box(modifier.fillMaxSize()) {
        HomeScreen(
            state = state,
            modifier = Modifier.fillMaxSize(),
            onRetry = viewModel::retry,
            onNotificationsClick = onNotificationsClick,
            onAttendanceClick = { interactionViewModel.openSheet(HomeSheet.AttendanceCode) },
            onSessionsClick = onSessionsClick,
            onReportClick = { interactionViewModel.openSheet(HomeSheet.ReportKind) },
            onScoreClick = onScoreClick,
            onNoticeClick = onNoticeClick,
            hasAttended = session != null && interaction.attendedSessionId == session.sessionId,
            hasReported = session != null && interaction.reportedSessionId == session.sessionId,
        )
        if (interaction.isReportComplete) {
            YappToast(
                text = stringResource(R.string.home_report_complete),
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                variant = YappToastVariant.Home,
                showIcon = false,
                actionText = stringResource(R.string.home_confirm),
                onAction = interactionViewModel::acknowledgeReportComplete,
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
        ) {
            HomeSheetContent(
                state = interaction,
                onCodeChange = interactionViewModel::updateCode,
                onReasonChange = interactionViewModel::updateReason,
                onReportChoice = interactionViewModel::selectReport,
                onLateDuration = interactionViewModel::selectLateDuration,
                onConfirm = {
                    when (interaction.sheet) {
                        HomeSheet.AttendanceCode ->
                            session?.let {
                                interactionViewModel.verifyAttendance(it.sessionId)
                            }
                        HomeSheet.ReportReason -> interactionViewModel.submitReport()
                        else -> interactionViewModel.advanceReport()
                    }
                },
            )
        }
    }
}
