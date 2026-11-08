package com.yapp.plus.feature.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.home.components.HomeQuickActions
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoPendingDetails
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoReceiptDetails
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoResultNotice
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoResultSheet
import com.yapp.plus.feature.main.home.components.HomeTeamPhotoSessionCard
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoReceiptUiModel
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoResultSheetState.UploadFailed
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoSessionUiState
import java.time.LocalDate
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Preview(widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoSubmittedPreview() =
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamPhotoSubmittedScreen(
            receipt = teamPhotoPreviewReceipt(),
            onBack = {},
            onConfirm = {},
            modifier = Modifier.systemBarsPadding(),
        )
    }

@Preview(widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoPendingPreview() = TeamPhotoHomePreview(state = teamPhotoPreviewPending())

@Preview(widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoApprovedPreview() =
    TeamPhotoHomePreview(
        state = HomeTeamPhotoSessionUiState.Approved,
        sheet = HomeTeamPhotoResultSheetState.Approved,
    )

@Preview(widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoRejectedPreview() =
    TeamPhotoHomePreview(
        sheet =
            HomeTeamPhotoResultSheetState.Rejected(
                retryDeadlineLabel = stringResource(R.string.home_team_result_preview_deadline),
                reason = stringResource(R.string.home_team_result_preview_reason),
                canRetry = true,
            ),
    )

@Preview(widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoNotSelectedPreview() {
    val reason = stringResource(R.string.home_team_result_preview_reason)
    TeamPhotoHomePreview(sheet = HomeTeamPhotoResultSheetState.ConfirmedAbsent(reason))
}

@Preview(widthDp = 393, heightDp = 756)
@Composable
fun TeamPhotoUploadFailurePreview() = TeamPhotoHomePreview(sheet = UploadFailed)

@Composable
private fun TeamPhotoHomePreview(
    state: HomeTeamPhotoSessionUiState = HomeTeamPhotoSessionUiState.AwaitingPhoto,
    sheet: HomeTeamPhotoResultSheetState? = null,
) {
    val sessionDate = LocalDate.of(2026, 11, 7)
    val backdrop = Modifier.fillMaxSize().background(YappHomeColor.background)
    YappTheme(darkTheme = false, dynamicColor = false) {
        Column(backdrop.systemBarsPadding()) {
            YappMainHeader(
                title = stringResource(R.string.attendance_home_title),
                iconResource = DesignSystemR.drawable.ic_notification,
                iconDescription = stringResource(R.string.attendance_home_notifications),
                iconTint = YappHomeColor.subtle,
                leadingContent = {
                    Image(
                        painterResource(DesignSystemR.drawable.graphic_yapp_logo),
                        stringResource(R.string.attendance_home_title),
                        Modifier.size(width = 80.dp, height = 30.dp),
                    )
                },
            )
            HomeTeamPhotoSessionCard(
                state = state,
                weekStartDate = sessionDate.minusDays(6),
                sessionDate = sessionDate,
                today = sessionDate,
                onUpload = {},
                onViewPhoto = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                thumbnail = painterResource(R.drawable.home_team_result_thumbnail),
            )
            HomeQuickActions({}, {}, Modifier.padding(horizontal = 16.dp))
        }
        if (sheet != null) {
            HomeTeamPhotoResultSheet(
                state = sheet,
                onConfirm = {},
                onRetry = {},
                onContactStaff = {},
                onDismiss = {},
            )
        }
    }
}

@Composable
private fun teamPhotoPreviewReceipt() =
    HomeTeamPhotoReceiptUiModel(
        teamName = stringResource(R.string.home_team_result_preview_team),
        uploadTimeLabel = stringResource(R.string.home_team_result_preview_time),
        attendeeCount = 4,
        teamMemberCount = 5,
        attendeeNamesLabel = stringResource(R.string.home_team_result_preview_members),
    )

@Composable
private fun teamPhotoPreviewPending() =
    HomeTeamPhotoSessionUiState.Pending(
        uploadTimeLabel = stringResource(R.string.home_team_result_preview_time),
        uploaderName = stringResource(R.string.home_team_result_preview_uploader),
        attendeeCount = 4,
    )

@Preview(widthDp = 353, showBackground = true)
@Composable
fun TeamPhotoReceiptDetailsPreview() {
    HomeTeamPhotoReceiptDetails(teamPhotoPreviewReceipt())
}

@Preview(widthDp = 321, showBackground = true)
@Composable
fun TeamPhotoPendingDetailsPreview() {
    HomeTeamPhotoPendingDetails(
        state = teamPhotoPreviewPending(),
        thumbnail = painterResource(R.drawable.home_team_result_thumbnail),
        onViewPhoto = {},
    )
}

@Preview(widthDp = 337, showBackground = true)
@Composable
fun TeamPhotoResultNoticePreview() {
    HomeTeamPhotoResultNotice(stringResource(R.string.home_team_result_size_notice))
}
