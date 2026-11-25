package com.yapp.plus.feature.main.home.team

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.team.components.HomeTeamPhotoGuideSheet
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoMemberUiModel
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoSelectionUiState

@Preview(name = "Team photo guide", widthDp = 393, heightDp = 756, showBackground = true)
@Composable
fun TeamPhotoGuidePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoGuideSheet(
            sessionTimeLabel = stringResource(R.string.home_team_photo_preview_time),
            onChooseFromAlbum = {},
            onTakePhoto = {},
            onDismiss = {},
        )
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
        initialMembers = teamPhotoPreviewMembers().map { it.copy(isCurrentUser = false) },
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
        errorResource = R.string.home_team_photo_invalid,
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
        )
    }
}

@Composable
internal fun teamPhotoPreviewMembers(): List<HomeTeamPhotoMemberUiModel> =
    listOf(
        HomeTeamPhotoMemberUiModel(
            membershipId = 10,
            name = stringResource(R.string.home_team_photo_preview_ios_name),
            roleLabel = "iOS",
            isCurrentUser = true,
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 11,
            name = stringResource(R.string.home_team_photo_preview_android_name),
            roleLabel = "Android",
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 12,
            name = stringResource(R.string.home_team_photo_preview_server_name),
            roleLabel = "Server",
        ),
    )
