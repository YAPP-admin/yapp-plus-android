package com.yapp.plus.feature.main.home.team

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage
import com.yapp.plus.feature.main.home.team.components.HomeTeamSubmittedPhotoViewer
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoViewerUiState
import java.time.Instant

@Preview(
    name = "Team attendance photo viewer",
    widthDp = 393,
    heightDp = 852,
    showSystemUi = true,
)
@Composable
fun TeamAttendancePhotoViewerPreview() {
    val context = LocalContext.current
    val image =
        remember(context) {
            TeamAttendancePhotoImage(
                url =
                    "android.resource://${context.packageName}/" +
                        R.drawable.home_team_result_thumbnail,
                expiresAt = Instant.MAX,
            )
        }
    YappTheme(darkTheme = false, dynamicColor = false) {
        HomeTeamSubmittedPhotoViewer(
            state = HomeTeamPhotoViewerUiState.Content(image),
            onClose = {},
        )
    }
}
