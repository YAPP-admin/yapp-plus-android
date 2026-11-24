package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.domain.attendance.AttendanceReportPolicy
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.main.home.components.HomeReportConfirmation
import com.yapp.plus.feature.main.home.components.HomeReportSheet
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onNotificationsClick: (() -> Unit)? = null,
    onSessionsClick: () -> Unit = {},
    onScoreClick: () -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    onAttendanceComplete: () -> Unit = {},
    onContactStaff: () -> Unit = {},
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionViewModel: HomeInteractionViewModel = hiltViewModel()
    val interaction by interactionViewModel.uiState.collectAsStateWithLifecycle()
    val teamViewModel: HomeTeamAttendanceViewModel = hiltViewModel()
    val team by teamViewModel.state.collectAsStateWithLifecycle()
    val session = (state.currentWeekSchedule as? AttendanceUiState.Content)?.value
    val teamSession = session?.takeIf { it.type == SessionType.TEAM }
    LaunchedEffect(state.currentWeekSchedule) {
        if (state.currentWeekSchedule != AttendanceUiState.Loading) teamViewModel.refresh(teamSession)
    }
    DisposableEffect(teamViewModel) {
        onDispose(teamViewModel::clear)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val now by produceState(Instant.now(), session?.sessionId) {
        while (true) {
            value = Instant.now()
            delay(1000)
        }
    }
    val canReport = AttendanceReportPolicy.canSubmit(session?.sessionDate, session?.startTime, now)

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (interactionViewModel.uiState.value.sheet == HomeSheet.None &&
            !teamViewModel.state.value.isActive
        ) {
            viewModel.loadHomeData()
        }
    }

    val verifiedSessionId = interaction.verifiedSessionId
    LaunchedEffect(verifiedSessionId, lifecycleOwner) {
        if (verifiedSessionId == null) return@LaunchedEffect
        viewModel.markAttendanceVerified(verifiedSessionId)
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            onAttendanceComplete()
            interactionViewModel.acknowledgeAttendanceComplete()
        }
    }

    LaunchedEffect(interaction.errorResource) {
        if (interaction.errorResource == R.string.home_report_session_changed) {
            viewModel.retryCurrentWeekSchedule()
        }
    }
    Box(modifier.fillMaxSize()) {
        HomeScreen(
            state = state.currentWeekSchedule,
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxSize(),
            onRetry = viewModel::retryCurrentWeekSchedule,
            noticeState = state.weeklyNotices,
            onNoticeRetry = viewModel::retryWeeklyNotices,
            onNotificationsClick = onNotificationsClick,
            onAttendanceClick = {
                session?.takeUnless { it.type == SessionType.TEAM }?.let {
                    interactionViewModel.openAttendanceCode(it.sessionId)
                }
            },
            onSessionsClick = onSessionsClick,
            onScoreClick = onScoreClick,
            onReportClick = interactionViewModel::openReport,
            onNoticeClick = onNoticeClick,
            teamSessionCard =
                teamSession?.let { target ->
                    {
                        HomeTeamAttendanceCard(
                            session = target,
                            state = team,
                            now = now,
                            onGuide = { teamViewModel.openGuide() },
                            onReload = { teamViewModel.refresh(target) },
                            onContact = onContactStaff,
                            onViewPhoto = teamViewModel::openSubmittedPhoto,
                        )
                    }
                },
            distanceContent = {
                session?.coordinates?.let { coordinates ->
                    key(session.sessionId, coordinates) {
                        HomeDistanceRoute(coordinates)
                    }
                }
            },
        )
        if (interaction.isReportComplete) {
            HomeReportConfirmation(
                interactionViewModel::acknowledgeReportComplete,
                Modifier.align(Alignment.BottomCenter).padding(16.dp),
            )
        }
    }
    if (teamSession != null) {
        HomeTeamAttendanceOverlays(teamSession, team, teamViewModel) {
            if (!teamViewModel.state.value.isSubmitting) {
                teamViewModel.closeFlow()
                teamViewModel.refresh(teamSession)
                viewModel.retryCurrentWeekSchedule()
            }
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
                    {
                        interactionViewModel.confirmReport(session?.sessionDate, session?.startTime)
                    },
                    canSubmit = canReport,
                    unavailableMessage = if (canReport) null else R.string.home_report_closed,
                )
            } else {
                HomeAttendanceCodeRoute(
                    session = session,
                    state = interaction,
                    viewModel = interactionViewModel,
                    onRefreshSession = viewModel::retryCurrentWeekSchedule,
                )
            }
        }
    }
}
