package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.Approved
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.AwaitingPhoto
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.BeforeStart
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.Closed
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.Loading
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.LookupFailed
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.NotEligible
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.Pending
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.ResultAvailable
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.Reviewing
import java.time.LocalDate
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun HomeTeamPhotoSessionCard(
    state: HomeTeamPhotoSessionUiState,
    sessionTimeLabel: String,
    weekStartDate: LocalDate,
    sessionDate: LocalDate?,
    today: LocalDate,
    onUpload: () -> Unit,
    onRetry: () -> Unit,
    onReviewResult: () -> Unit,
    modifier: Modifier = Modifier,
    onViewPhoto: (() -> Unit)? = null,
    thumbnail: (@Composable (Modifier) -> Unit)? = null,
    onReplace: (() -> Unit)? = null,
) {
    val sessionDayContent: (@Composable (Int) -> Unit)? =
        if (state == Approved) {
            { day ->
                Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.ic_home_team_result_attended),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                    YappText(day.toString(), YappTypography.scaleT2Medium, YappHomeColor.surface)
                }
                YappText(
                    text = stringResource(R.string.home_team_result_attended_marker),
                    style = YappHomeTypography.day,
                    color = YappHomeColor.brand,
                    modifier = Modifier.fillMaxWidth().background(YappMainColor.brandWeak),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            null
        }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = YappHomeColor.surface,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(DesignSystemR.drawable.ic_map_pin),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = YappHomeColor.placeholder,
                    )
                    val location =
                        stringResource(R.string.home_team_result_session_location, sessionTimeLabel)
                    YappText(location, YappTypography.scaleT3Medium, YappHomeColor.subtle)
                }
                val title = stringResource(R.string.home_team_result_session_title)
                YappText(title, YappTypography.scaleT7Bold, YappHomeColor.text)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                HomeWeekCalendar(
                    weekStartDate = weekStartDate,
                    sessionDate = sessionDate,
                    today = today,
                    dayHeight = 58.dp,
                    sessionDayContent = sessionDayContent,
                )
                if (state is Pending) HomeTeamPhotoPendingDetails(state, thumbnail, onViewPhoto)
            }
            HomeTeamPhotoSessionAction(
                state = state,
                onUpload = onUpload,
                onRetry = onRetry,
                onReviewResult = onReviewResult,
            )
            if (state is Pending && onReplace != null) {
                YappButton(
                    text = stringResource(R.string.home_team_result_retry),
                    onClick = onReplace,
                    modifier = Modifier.fillMaxWidth(),
                    variant = YappButtonVariant.SolidNeutral,
                    size = YappButtonSize.HomeSecondary,
                )
            }
        }
    }
}

@Composable
internal fun HomeTeamPhotoSessionAction(
    state: HomeTeamPhotoSessionUiState,
    onUpload: () -> Unit,
    onRetry: () -> Unit,
    onReviewResult: () -> Unit,
) {
    if (state == Loading) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = YappHomeColor.brand,
            )
        }
        return
    }
    if (state == LookupFailed) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            HomeSessionError(
                onRetry = onRetry,
                messageResource = R.string.home_team_result_lookup_error,
            )
        }
        return
    }
    val label =
        when (state) {
            AwaitingPhoto -> stringResource(R.string.home_team_result_upload)
            is BeforeStart -> stringResource(R.string.home_session_starts, state.startTimeLabel)
            Closed -> stringResource(R.string.attendance_status_not_open)
            NotEligible -> stringResource(R.string.attendance_status_not_eligible)
            Reviewing, is Pending -> stringResource(R.string.home_team_result_pending_action)
            Approved -> stringResource(R.string.home_attended)
            ResultAvailable -> stringResource(R.string.home_team_result_check)
            Loading, LookupFailed -> return
        }
    val action =
        when (state) {
            AwaitingPhoto -> onUpload
            ResultAvailable -> onReviewResult
            else -> null
        }
    YappButton(
        text = label,
        onClick = { action?.invoke() },
        modifier = Modifier.fillMaxWidth(),
        variant = YappButtonVariant.SolidNeutral,
        size = YappButtonSize.HomeSecondary,
        enabled = action != null,
    )
}

@Composable
internal fun HomeTeamPhotoPendingDetails(
    state: Pending,
    thumbnail: (@Composable (Modifier) -> Unit)?,
    onViewPhoto: (() -> Unit)?,
) {
    val photoAction =
        onViewPhoto?.let {
            Modifier.clickable(
                onClickLabel = stringResource(R.string.home_team_result_view_photo),
                role = Role.Button,
                onClick = it,
            )
        } ?: Modifier
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(YappMainColor.layerFill)
                .then(photoAction)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        thumbnail?.invoke(Modifier.size(41.dp).clip(RoundedCornerShape(6.dp)))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            val title = stringResource(R.string.home_team_result_pending_title)
            YappText(title, YappTypography.scaleT4Bold, YappHomeColor.text)
            val uploader = state.uploaderName?.takeIf { it.isNotBlank() }
            val details =
                if (uploader == null) {
                    stringResource(
                        R.string.home_team_result_pending_details_without_uploader,
                        state.uploadTimeLabel,
                        state.attendeeCount,
                    )
                } else {
                    stringResource(
                        R.string.home_team_result_pending_details,
                        state.uploadTimeLabel,
                        uploader,
                        state.attendeeCount,
                    )
                }
            YappText(
                details,
                YappTypography.scaleT2Medium,
                YappHomeColor.subtle,
            )
        }
        if (onViewPhoto != null) {
            Image(
                painter = painterResource(R.drawable.ic_home_team_result_camera),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
