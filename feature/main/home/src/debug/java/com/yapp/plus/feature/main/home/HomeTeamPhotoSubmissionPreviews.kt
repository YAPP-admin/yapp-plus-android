package com.yapp.plus.feature.main.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoGuideSheet
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoMemberUiModel
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSelectionUiState

@Preview(name = "Team photo guide", widthDp = 393, heightDp = 756, showBackground = true)
@Composable
fun TeamPhotoGuidePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Box(Modifier.fillMaxSize()) {
            HomeScreen(
                state = HomePreviewData.state(AttendanceStatusUiModel.Available),
                modifier = Modifier.systemBarsPadding(),
                now = HomePreviewData.now,
            )
            HomeTeamPhotoGuideSheet(
                sessionTimeLabel = stringResource(R.string.home_team_photo_preview_time),
                onChooseFromAlbum = {},
                onTakePhoto = {},
                onDismiss = {},
            )
        }
    }
}

@Preview(name = "Team photo selection", widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoSelectionPreview() {
    TeamPhotoSelectionStatePreview(initialMembers = teamPhotoPreviewMembers())
}

@Preview(name = "Team photo no selection", widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoUnselectedPreview() {
    TeamPhotoSelectionStatePreview(
        initialMembers = teamPhotoPreviewMembers().map { it.copy(isSelected = false) },
    )
}

@Preview(name = "Team photo without current user", widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoWithoutCurrentUserPreview() {
    TeamPhotoSelectionStatePreview(
        initialMembers =
            teamPhotoPreviewMembers().map { member ->
                if (member.isCurrentUser) member.copy(isSelected = false) else member
            },
    )
}

@Preview(name = "Team photo submitting", widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoSubmittingPreview() {
    TeamPhotoSelectionStatePreview(
        initialMembers = teamPhotoPreviewMembers(),
        isSubmitting = true,
    )
}

@Preview(name = "Team photo submission error", widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoSubmissionErrorPreview() {
    TeamPhotoSelectionStatePreview(
        initialMembers = teamPhotoPreviewMembers(),
        errorResource = R.string.home_submit_error,
    )
}

@Composable
private fun TeamPhotoSelectionStatePreview(
    initialMembers: List<HomeTeamPhotoMemberUiModel>,
    isSubmitting: Boolean = false,
    @StringRes errorResource: Int? = null,
) {
    var members by remember(initialMembers) { mutableStateOf(initialMembers) }
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoSelectionScreen(
            state =
                HomeTeamPhotoSelectionUiState(
                    members = members,
                    canConfirm = members.any { it.isCurrentUser && it.isSelected },
                    isSubmitting = isSubmitting,
                    errorResource = errorResource,
                ),
            photo = painterResource(R.drawable.home_team_photo_preview_placeholder),
            onBack = {},
            onMemberSelectionChange = { membershipId, isSelected ->
                members =
                    members.map { member ->
                        if (member.membershipId == membershipId) {
                            member.copy(isSelected = isSelected)
                        } else {
                            member
                        }
                    }
            },
            onConfirm = {},
            modifier = Modifier.systemBarsPadding(),
        )
    }
}

@Composable
internal fun teamPhotoPreviewMembers(): List<HomeTeamPhotoMemberUiModel> =
    listOf(
        HomeTeamPhotoMemberUiModel(
            membershipId = 1L,
            name = stringResource(R.string.home_team_photo_preview_pm_name),
            roleLabel = stringResource(R.string.home_team_photo_preview_pm_role),
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 2L,
            name = stringResource(R.string.home_team_photo_preview_design_name),
            roleLabel = stringResource(R.string.home_team_photo_preview_design_role),
            isCurrentUser = true,
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 3L,
            name = stringResource(R.string.home_team_photo_preview_android_name),
            roleLabel = stringResource(R.string.home_team_photo_preview_android_role),
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 4L,
            name = stringResource(R.string.home_team_photo_preview_ios_name),
            roleLabel = stringResource(R.string.home_team_photo_preview_ios_role),
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 5L,
            name = stringResource(R.string.home_team_photo_preview_server_name),
            roleLabel = stringResource(R.string.home_team_photo_preview_server_role),
            isSelected = true,
        ),
    )
