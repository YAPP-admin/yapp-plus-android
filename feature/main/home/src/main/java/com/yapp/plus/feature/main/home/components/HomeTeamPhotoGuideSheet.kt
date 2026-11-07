package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R

private val GuideIllustrationWidth = 337.dp
private const val GUIDE_ILLUSTRATION_ASPECT_RATIO = 337f / 158f

@Composable
internal fun HomeTeamPhotoGuideSheet(
    sessionTimeLabel: String,
    onChooseFromAlbum: () -> Unit,
    onTakePhoto: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    YappBottomSheet(
        onDismissRequest = onDismiss,
        onConfirm = onTakePhoto,
        onDismiss = onDismiss,
        modifier = modifier,
        showHandle = true,
        title = null,
        body = null,
        confirmText = null,
        dismissText = null,
        showActions = false,
        variant = YappBottomSheetVariant.Home,
    ) {
        HomeTeamPhotoGuideContent(
            sessionTimeLabel = sessionTimeLabel,
            onChooseFromAlbum = onChooseFromAlbum,
            onTakePhoto = onTakePhoto,
        )
    }
}

@Composable
internal fun HomeTeamPhotoGuideContent(
    sessionTimeLabel: String,
    onChooseFromAlbum: () -> Unit,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            YappText(
                text = stringResource(R.string.home_team_photo_guide_title),
                style = YappTypography.scaleT7Bold,
                color = YappHomeColor.text,
                modifier = Modifier.semantics { heading() },
            )
            YappText(
                text = stringResource(R.string.home_team_photo_guide_description),
                style = YappTypography.scaleT4Medium,
                color = YappHomeColor.muted,
            )
        }
        Image(
            painter = painterResource(R.drawable.home_team_photo_guide_illustration),
            contentDescription = null,
            modifier =
                Modifier
                    .widthIn(max = GuideIllustrationWidth)
                    .fillMaxWidth()
                    .aspectRatio(GUIDE_ILLUSTRATION_ASPECT_RATIO),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HomeTeamPhotoGuideInstruction(
                text = stringResource(R.string.home_team_photo_guide_members),
            )
            HomeTeamPhotoGuideInstruction(
                text = stringResource(R.string.home_team_photo_guide_time, sessionTimeLabel),
            )
            HomeTeamPhotoGuideInstruction(
                text = stringResource(R.string.home_team_photo_guide_review),
            )
        }
        HomeTeamPhotoGuideActions(onChooseFromAlbum, onTakePhoto)
    }
}

@Composable
internal fun HomeTeamPhotoGuideInstruction(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_home_team_photo_guide_check),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        YappText(text, YappTypography.scaleT3Medium, YappHomeColor.muted)
    }
}

@Composable
internal fun HomeTeamPhotoGuideActions(
    onChooseFromAlbum: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            onClick = onChooseFromAlbum,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = YappHomeColor.background,
        ) {
            Box(
                modifier =
                    Modifier
                        .heightIn(min = 52.dp)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                YappText(
                    text = stringResource(R.string.home_team_photo_choose_from_album),
                    style = YappTypography.scaleT5Bold,
                    color = YappHomeColor.subtle,
                    textAlign = TextAlign.Center,
                )
            }
        }
        YappButton(
            text = stringResource(R.string.home_team_photo_take_photo),
            onClick = onTakePhoto,
            modifier = Modifier.fillMaxWidth(),
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
        )
    }
}
