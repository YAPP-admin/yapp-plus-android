package com.yapp.plus.feature.main.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.teamPhotoPreviewPending
import com.yapp.plus.feature.main.home.teamPhotoPreviewReceipt

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
