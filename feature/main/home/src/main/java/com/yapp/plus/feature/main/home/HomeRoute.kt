package com.yapp.plus.feature.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.components.toast.YappToastData
import com.yapp.plus.core.designsystem.components.toast.YappToastHost
import com.yapp.plus.core.designsystem.components.toast.YappToastManager
import com.yapp.plus.core.designsystem.components.toast.YappToastType
import com.yapp.plus.core.ui.ObserveAsEvents
import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.domain.attendance.AttendanceReportPolicy
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.main.home.approval.HomeTeamApprovalRoute
import com.yapp.plus.feature.main.home.attendance.HomeAttendanceCodeRoute
import com.yapp.plus.feature.main.home.components.HomeReportSheet
import com.yapp.plus.feature.main.home.components.HomeSheetContent
import com.yapp.plus.feature.main.home.components.HomeWeekCalendar
import com.yapp.plus.feature.main.home.model.HomeInteractionEvent
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.HomeSubmissionStage
import com.yapp.plus.feature.main.home.model.shouldShowReportTip
import com.yapp.plus.feature.main.home.model.teamZone
import com.yapp.plus.feature.main.home.team.HomeTeamAttendanceRoute
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun HomeRoute(
    toastManager: YappToastManager,
    modifier: Modifier = Modifier,
    onNotificationsClick: (() -> Unit)? = null,
    onSessionsClick: () -> Unit = {},
    onNoticeClick: (Long) -> Unit = {},
    onAttendanceComplete: () -> Unit = {},
    onContactStaff: () -> Unit = {},
    afterpartyBanner: (@Composable () -> Unit)? = null,
    teamApprovalViewModelStoreOwner: ViewModelStoreOwner? = null,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionViewModel: HomeInteractionViewModel = hiltViewModel()
    val interaction by interactionViewModel.uiState.collectAsStateWithLifecycle()

    val reportCompleteText = stringResource(R.string.home_report_complete)
    val session = (state.currentWeekSchedule as? AttendanceUiState.Content)?.value

    val lifecycleOwner = LocalLifecycleOwner.current
    val now by produceState(Instant.now(), session?.sessionId) {
        while (true) {
            value = Instant.now()
            delay(1000)
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
    HomeTeamApprovalRoute(
        sessionId = session?.takeIf { it.type == SessionType.TEAM }?.sessionId,
        viewModelStoreOwner = teamApprovalViewModelStoreOwner,
    ) { isAcknowledged, onAcknowledgeApproval ->
        HomeTeamAttendanceRoute(
            schedule = state.currentWeekSchedule,
            now = now,
            canShowApproval = interaction.sheet == HomeSheet.None && !isAcknowledged,
            onAcknowledgeApproval = onAcknowledgeApproval,
            onContactStaff = onContactStaff,
            onRefreshSchedule = viewModel::retryCurrentWeekSchedule,
            weekCalendar = { target ->
                HomeWeekCalendar(
                    weekStartDate = checkNotNull(target.weekStartDate),
                    sessionDate = target.sessionDate,
                    today = now.atZone(teamZone).toLocalDate(),
                    isSessionAttended =
                        target.attendanceStatus == AttendanceStatusUiModel.CheckedIn,
                    dayHeight = 58.dp,
                )
            },
        ) { teamSessionCard, showTeamPhotoUploadTip, onRefreshTeam ->
            Box(modifier.fillMaxSize()) {
                HomeScreen(
                    state = state.currentWeekSchedule,
                    isLoading = state.isLoading,
                    isRefreshing = state.isRefreshing,
                    onRefresh = {
                        viewModel.refreshHome()
                        onRefreshTeam()
                    },
                    modifier = Modifier.fillMaxSize(),
                    noticeState = state.weeklyNotices,
                    afterpartyBanner = afterpartyBanner,
                    showReportTip =
                        shouldShowReportTip(session?.sessionDate, session?.startTime, now),
                    showTeamPhotoUploadTip = showTeamPhotoUploadTip,
                    onNotificationsClick = onNotificationsClick,
                    onAttendanceClick = {
                        session?.takeUnless { it.type == SessionType.TEAM }?.let {
                            interactionViewModel.openAttendanceCode(it.sessionId)
                        }
                    },
                    onSessionsClick = onSessionsClick,
                    onReportClick = interactionViewModel::openReport,
                    onNoticeClick = onNoticeClick,
                    teamSessionCard = teamSessionCard,
                )
                YappToastHost(toastManager = toastManager)
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
                val canReport =
                    AttendanceReportPolicy.canSubmit(session?.sessionDate, session?.startTime, now)
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
                    sessionId = session?.sessionId,
                    coordinates = session?.coordinates,
                    isAvailable = session?.attendanceStatus == AttendanceStatusUiModel.Available,
                    isCheckingLocation =
                        interaction.submissionStage == HomeSubmissionStage.RequestingLocationPermission ||
                            interaction.submissionStage == HomeSubmissionStage.CheckingLocation,
                    locationIssue = interaction.locationIssue,
                    sheetContent = { isCheckingLocation, locationIssue, onConfirm, onLocationRecovery ->
                        HomeSheetContent(
                            state = interaction,
                            onCodeChange = interactionViewModel::updateCode,
                            onConfirm = onConfirm,
                            isCheckingLocation = isCheckingLocation,
                            locationIssue = locationIssue,
                            onLocationRecovery = onLocationRecovery,
                        )
                    },
                    onCancelLocation = interactionViewModel::cancelAttendanceLocation,
                    onConfirmAttendance = { hasPrecisePermission ->
                        interactionViewModel.confirmAttendance(session, hasPrecisePermission)
                    },
                    onPermissionResult = { hasPrecisePermission, hasApproximatePermission ->
                        interactionViewModel.onAttendancePermissionResult(
                            session = session,
                            hasPrecisePermission = hasPrecisePermission,
                            hasApproximatePermission = hasApproximatePermission,
                        )
                    },
                    onLocationSettingsUnavailable = interactionViewModel::onLocationSettingsUnavailable,
                    onRefreshSession = viewModel::retryCurrentWeekSchedule,
                )
            }
        }
    }
    ObserveAsEvents(interactionViewModel.events) { event ->
        when (event) {
            HomeInteractionEvent.ReportCompleted ->
                toastManager.tryShow(
                    YappToastData(
                        text = reportCompleteText,
                        type = YappToastType.Default,
                    ),
                )
        }
    }
}
