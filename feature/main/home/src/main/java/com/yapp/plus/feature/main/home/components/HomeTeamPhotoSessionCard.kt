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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
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
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState.Pending
import java.time.LocalDate
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun HomeTeamPhotoSessionCard(
    state: HomeTeamPhotoSessionUiState,
    weekStartDate: LocalDate,
    sessionDate: LocalDate?,
    today: LocalDate,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
    onViewPhoto: (() -> Unit)? = null,
    thumbnail: Painter? = null,
) {
    val actionLabel =
        when (state) {
            AwaitingPhoto -> R.string.home_team_result_upload
            is Pending -> R.string.home_team_result_pending_action
            Approved -> R.string.home_attended
        }
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
                    YappText(
                        stringResource(R.string.home_team_result_session_location),
                        YappTypography.scaleT3Medium,
                        YappHomeColor.subtle,
                    )
                }
                YappText(
                    stringResource(R.string.home_team_result_session_title),
                    YappTypography.scaleT7Bold,
                    YappHomeColor.text,
                )
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
            YappButton(
                text = stringResource(actionLabel),
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth(),
                variant = YappButtonVariant.SolidNeutral,
                size = YappButtonSize.HomeSecondary,
                enabled = state == AwaitingPhoto,
            )
        }
    }
}

@Composable
internal fun HomeTeamPhotoPendingDetails(
    state: Pending,
    thumbnail: Painter?,
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
        if (thumbnail != null) {
            Image(
                painter = thumbnail,
                contentDescription = null,
                modifier = Modifier.size(41.dp).clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            YappText(
                stringResource(R.string.home_team_result_pending_title),
                YappTypography.scaleT4Bold,
                YappHomeColor.text,
            )
            YappText(
                stringResource(
                    R.string.home_team_result_pending_details,
                    state.uploadTimeLabel,
                    state.uploaderName,
                    state.attendeeCount,
                ),
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
