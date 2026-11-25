package com.yapp.plus.feature.main.home.team

import androidx.compose.runtime.Composable
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

@Composable
private fun TeamPhotoSelectionStatePreview(initialMembers: List<HomeTeamPhotoMemberUiModel>) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoSelectionScreen(
            state =
                HomeTeamPhotoSelectionUiState(
                    members = initialMembers,
                    canConfirm = initialMembers.any { it.isCurrentUser && it.isSelected },
                ),
            photo = painterResource(R.drawable.home_team_photo_preview_placeholder),
            onBack = {},
            onMemberSelectionChange = { _, _ -> },
            onConfirm = {},
        )
    }
}

@Composable
internal fun teamPhotoPreviewMembers(): List<HomeTeamPhotoMemberUiModel> =
    listOf(
        HomeTeamPhotoMemberUiModel(
            membershipId = 10,
            name = stringResource(R.string.home_team_photo_preview_member_1),
            roleLabel = "iOS",
            isCurrentUser = true,
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 11,
            name = stringResource(R.string.home_team_photo_preview_member_2),
            roleLabel = "Android",
            isSelected = true,
        ),
        HomeTeamPhotoMemberUiModel(
            membershipId = 12,
            name = stringResource(R.string.home_team_photo_preview_member_3),
            roleLabel = "Web",
        ),
    )
