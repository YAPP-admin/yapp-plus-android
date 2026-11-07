package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.HomeTeamPhotoSelectionActions
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSelectionUiState
import com.yapp.plus.feature.main.home.teamPhotoPreviewMembers

@Preview(name = "Team photo guide content", widthDp = 373, showBackground = true)
@Composable
fun TeamPhotoGuideContentPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoGuideContent(
            sessionTimeLabel = stringResource(R.string.home_team_photo_preview_time),
            onChooseFromAlbum = {},
            onTakePhoto = {},
            modifier = Modifier.padding(18.dp),
        )
    }
}

@Preview(name = "Team photo guide instruction", showBackground = true)
@Composable
fun TeamPhotoGuideInstructionPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoGuideInstruction(
            text = stringResource(R.string.home_team_photo_guide_members),
        )
    }
}

@Preview(name = "Team photo guide buttons", widthDp = 337, showBackground = true)
@Composable
fun TeamPhotoGuideActionsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoGuideActions(onChooseFromAlbum = {}, onTakePhoto = {})
    }
}

@Preview(name = "Team photo image", widthDp = 361, showBackground = true)
@Composable
fun TeamPhotoImagePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoPreview(painterResource(R.drawable.home_team_photo_preview_placeholder))
    }
}

@Preview(name = "Team photo member rows", widthDp = 361, showBackground = true)
@Composable
fun TeamPhotoMemberRowsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            teamPhotoPreviewMembers().forEach { member ->
                HomeTeamPhotoMemberRow(member = member, onSelectionChange = {})
            }
        }
    }
}

@Preview(name = "Team photo selection confirm", widthDp = 393, showBackground = true)
@Composable
fun TeamPhotoSelectionActionsPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoSelectionActions(
            state =
                HomeTeamPhotoSelectionUiState(
                    members = teamPhotoPreviewMembers(),
                    canConfirm = true,
                ),
            onConfirm = {},
        )
    }
}
