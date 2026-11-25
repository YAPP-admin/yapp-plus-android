package com.yapp.plus.feature.main.home.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoGuideSheet
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoResultSheet
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoSessionCard
import com.yapp.plus.feature.main.home.team.components.HomeTeamSubmittedPhotoDialog
import com.yapp.plus.feature.main.home.team.components.HomeTeamSubmittedPhotoThumbnail
import com.yapp.plus.feature.main.home.team.model.HomeTeamAttendanceUiState
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoResultSheetState
import com.yapp.plus.feature.main.home.team.model.teamTimeFormat
import java.time.Instant

@Composable
fun HomeTeamAttendanceCard(
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
    HomeTeamPhotoSessionCard(
        state = state.card(session, now),
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
    if (viewingResult) {
        val result =
            when {
                state.isAbsent -> HomeTeamPhotoResultSheetState.ConfirmedAbsent(reason = null)
                photo?.status == TeamAttendancePhotoStatus.REJECTED ->
                    HomeTeamPhotoResultSheetState.Rejected(
                        retryDeadlineLabel = session.endTime?.takeIf { canUpload }?.format(teamTimeFormat),
                        reason = null,
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
fun HomeTeamAttendanceOverlays(
    session: CurrentWeekSessionUiModel,
    state: HomeTeamAttendanceUiState,
    viewModel: HomeTeamAttendanceViewModel,
    onFinish: () -> Unit,
    errorContent: @Composable () -> Unit = {},
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
                                    errorContent()
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
    }
}
