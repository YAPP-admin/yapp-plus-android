package com.yapp.plus.feature.main.home.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.domain.session.SessionType
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoGuideSheet
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoResultSheet
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoSessionCard
import com.yapp.plus.feature.main.home.team.components.HomeTeamSessionError
import com.yapp.plus.feature.main.home.team.components.HomeTeamSubmittedPhotoDialog
import com.yapp.plus.feature.main.home.team.components.HomeTeamSubmittedPhotoThumbnail
import com.yapp.plus.feature.main.home.team.model.HomeTeamAttendanceUiState
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoResultSheetState
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoSessionUiState
import com.yapp.plus.feature.main.home.team.model.teamTimeFormat
import java.time.Instant

@Composable
fun HomeTeamAttendanceRoute(
    schedule: AttendanceUiState<CurrentWeekSessionUiModel>,
    now: Instant,
    canShowApproval: Boolean,
    onAcknowledgeApproval: () -> Unit,
    onContactStaff: () -> Unit,
    onRefreshSchedule: () -> Unit,
    weekCalendar: @Composable (CurrentWeekSessionUiModel) -> Unit,
    content: @Composable (
        teamSessionCard: (@Composable () -> Unit)?,
        showTeamPhotoUploadTip: Boolean,
        onRefreshTeam: () -> Unit,
    ) -> Unit,
) {
    val viewModel: HomeTeamAttendanceViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val session =
        (schedule as? AttendanceUiState.Content)?.value?.takeIf { it.type == SessionType.TEAM }

    LaunchedEffect(schedule) {
        when (schedule) {
            is AttendanceUiState.Content -> viewModel.updateSession(session)
            AttendanceUiState.Empty, AttendanceUiState.Inactive -> viewModel.updateSession(null)
            AttendanceUiState.Loading, AttendanceUiState.Error -> Unit
        }
    }
    DisposableEffect(viewModel) {
        onDispose(viewModel::discardFlow)
    }

    val teamSessionCard: (@Composable () -> Unit)? =
        session?.let { target ->
            {
                HomeTeamAttendanceCard(
                    session = target,
                    state = state,
                    now = now,
                    weekCalendar = { weekCalendar(target) },
                    onGuide = { viewModel.openGuide() },
                    onContact = onContactStaff,
                    onViewPhoto = viewModel::openSubmittedPhoto,
                )
            }
        }
    val showUploadTip =
        session != null && state.card(session, now) == HomeTeamPhotoSessionUiState.AwaitingPhoto
    content(teamSessionCard, showUploadTip) { if (session != null) viewModel.refresh() }

    if (session != null) {
        HomeTeamAttendanceOverlays(
            session = session,
            state = state,
            viewModel = viewModel,
            showApproval = canShowApproval && state.isCheckedIn(session),
            onAcknowledgeApproval = onAcknowledgeApproval,
        ) {
            if (!viewModel.state.value.isSubmitting) {
                viewModel.closeFlow()
                viewModel.refresh()
                onRefreshSchedule()
            }
        }
    }
}

@Composable
private fun HomeTeamAttendanceCard(
    session: CurrentWeekSessionUiModel,
    state: HomeTeamAttendanceUiState,
    now: Instant,
    weekCalendar: @Composable () -> Unit,
    onGuide: () -> Unit,
    onContact: () -> Unit,
    onViewPhoto: () -> Unit,
) {
    var viewingResult by remember(session.sessionId) { mutableStateOf(false) }
    val photo = state.photo
    val canUpload = session.canUploadTeamPhoto(now)
    val cardState = state.card(session, now)
    HomeTeamPhotoSessionCard(
        state = cardState,
        sessionTimeLabel = session.teamTimeLabel(),
        weekCalendar = weekCalendar,
        onUpload = onGuide,
        onReviewResult = { viewingResult = true },
        onViewPhoto = onViewPhoto.takeIf { photo?.image != null },
        thumbnail =
            photo?.image?.takeIf { it.expiresAt.isAfter(now) }?.let { image ->
                { modifier -> HomeTeamSubmittedPhotoThumbnail(image, modifier) }
            },
    )
    if (viewingResult && cardState != HomeTeamPhotoSessionUiState.Approved) {
        val result =
            when {
                state.isAbsent -> HomeTeamPhotoResultSheetState.ConfirmedAbsent(reason = null)
                photo?.status == TeamAttendancePhotoStatus.REJECTED ->
                    HomeTeamPhotoResultSheetState.Rejected(
                        retryDeadlineLabel = session.endTime?.takeIf { canUpload }?.format(teamTimeFormat),
                        reason = photo.rejectionReason,
                        canRetry = canUpload,
                    )
                else -> null
            }
        if (result != null) {
            HomeTeamPhotoResultSheet(
                state = result,
                onConfirm = { viewingResult = false },
                onRetry = {
                    viewingResult = false
                    onGuide()
                },
                onContactStaff = onContact,
                onDismiss = { viewingResult = false },
            )
        }
    }
}

@Composable
private fun HomeTeamAttendanceOverlays(
    session: CurrentWeekSessionUiModel,
    state: HomeTeamAttendanceUiState,
    viewModel: HomeTeamAttendanceViewModel,
    showApproval: Boolean,
    onAcknowledgeApproval: () -> Unit,
    onFinish: () -> Unit,
) {
    val pickPhoto = rememberHomeTeamPhotoPicker(viewModel)
    val bitmap = state.input?.preview
    when {
        state.photoViewer != null ->
            HomeTeamSubmittedPhotoDialog(
                state = state.photoViewer,
                onClose = viewModel::closeSubmittedPhoto,
            )
        state.guide ->
            HomeTeamPhotoGuideSheet(
                sessionTimeLabel = session.teamTimeLabel(),
                onChooseFromAlbum = { pickPhoto(false) },
                onTakePhoto = { pickPhoto(true) },
                onDismiss = viewModel::closeFlow,
                errorResource = state.error,
            )
        state.uploadFailed ->
            HomeTeamPhotoResultSheet(
                state = HomeTeamPhotoResultSheetState.UploadFailed,
                onConfirm = onFinish,
                onRetry = { viewModel.submit() },
            )
        bitmap != null || state.receipt != null ->
            Dialog(
                onDismissRequest = onFinish,
                properties =
                    DialogProperties(
                        usePlatformDefaultWidth = false,
                        dismissOnBackPress = !state.isSubmitting,
                    ),
            ) {
                when {
                    state.receipt != null -> HomeTeamPhotoSubmittedScreen(state.receipt, onFinish, onFinish)
                    state.roster == null ->
                        Surface {
                            Column(Modifier.padding(20.dp)) {
                                if (state.preparing) {
                                    CircularProgressIndicator()
                                } else {
                                    HomeTeamSessionError(R.string.home_team_result_lookup_error)
                                }
                            }
                        }
                    bitmap != null ->
                        HomeTeamPhotoSelectionScreen(
                            state = state.selection(),
                            photo = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) },
                            onBack = onFinish,
                            onMemberSelectionChange = viewModel::selectMember,
                            onConfirm = { viewModel.submit() },
                        )
                }
            }
        showApproval ->
            HomeTeamPhotoResultSheet(
                state = HomeTeamPhotoResultSheetState.Approved,
                onConfirm = onAcknowledgeApproval,
            )
    }
}
