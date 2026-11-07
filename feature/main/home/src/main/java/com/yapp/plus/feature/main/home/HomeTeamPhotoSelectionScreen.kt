package com.yapp.plus.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.main.YappDetailHeader
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.components.HomeSubmissionError
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoMemberRow
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoPreview
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSelectionUiState

@Composable
internal fun HomeTeamPhotoSelectionScreen(
    state: HomeTeamPhotoSelectionUiState,
    photo: Painter,
    onBack: () -> Unit,
    onMemberSelectionChange: (membershipId: Long, isSelected: Boolean) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappHomeColor.surface),
    ) {
        YappDetailHeader(
            title = stringResource(R.string.home_team_photo_selection_title),
            backDescription = stringResource(R.string.home_back),
            onBack = onBack,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                HomeTeamPhotoPreview(
                    photo = photo,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    YappText(
                        text = stringResource(R.string.home_team_photo_members_title),
                        style = YappTypography.scaleT6Bold,
                        color = YappHomeColor.text,
                        modifier = Modifier.semantics { heading() },
                    )
                    YappText(
                        text = stringResource(R.string.home_team_photo_members_description),
                        style = YappTypography.scaleT4Medium,
                        color = YappHomeColor.muted,
                    )
                }
            }
            items(state.members, key = { it.membershipId }) { member ->
                HomeTeamPhotoMemberRow(
                    member = member,
                    onSelectionChange = { isSelected ->
                        onMemberSelectionChange(member.membershipId, isSelected)
                    },
                    isEnabled = !state.isSubmitting,
                )
            }
        }
        HomeTeamPhotoSelectionActions(state, onConfirm)
    }
}

@Composable
internal fun HomeTeamPhotoSelectionActions(
    state: HomeTeamPhotoSelectionUiState,
    onConfirm: () -> Unit,
) {
    val selectedCount = state.selectedCount
    val label =
        if (state.isSubmitting) {
            stringResource(R.string.home_team_photo_submitting)
        } else {
            stringResource(R.string.home_team_photo_confirm, selectedCount)
        }
    val progressIndicator: (@Composable () -> Unit)? =
        if (state.isSubmitting) {
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = YappHomeColor.surface,
                    strokeWidth = 2.dp,
                )
            }
        } else {
            null
        }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        state.errorResource?.let { errorResource ->
            Column(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                HomeSubmissionError(stringResource(errorResource))
            }
        }
        YappButton(
            text = label,
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
            enabled = state.canConfirm && selectedCount > 0 && !state.isSubmitting,
            leadingIcon = progressIndicator,
        )
    }
}
