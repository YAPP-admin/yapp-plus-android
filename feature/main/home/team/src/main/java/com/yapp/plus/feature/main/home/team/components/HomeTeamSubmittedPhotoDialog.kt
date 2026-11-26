package com.yapp.plus.feature.main.home.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.yapp.plus.core.designsystem.modifier.WithoutRipple
import com.yapp.plus.core.designsystem.modifier.shimmerEffect
import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage
import com.yapp.plus.feature.main.home.team.R
import com.yapp.plus.feature.main.home.team.model.HomeTeamPhotoViewerUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun HomeTeamSubmittedPhotoDialog(
    state: HomeTeamPhotoViewerUiState,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        ConfigureBlackPhotoViewerSystemBars()
        HomeTeamSubmittedPhotoViewer(
            state = state,
            onClose = onClose,
        )
    }
}

@Composable
internal fun HomeTeamSubmittedPhotoViewer(
    state: HomeTeamPhotoViewerUiState,
    onClose: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            HomeTeamSubmittedPhotoContent(state)
        }
        WithoutRipple {
            IconButton(
                onClick = onClose,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 8.dp, end = 8.dp),
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_close),
                    contentDescription = stringResource(R.string.home_team_result_close),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun HomeTeamSubmittedPhotoContent(state: HomeTeamPhotoViewerUiState) {
    when (state) {
        HomeTeamPhotoViewerUiState.Loading ->
            Box(
                Modifier
                    .fillMaxSize()
                    .shimmerEffect(
                        baseColor = Color(0xFF2A2A2A),
                        shimmerColor = Color(0xFF3D3D3D),
                    ),
            )
        HomeTeamPhotoViewerUiState.Unavailable ->
            Text(stringResource(R.string.home_team_result_image_unavailable), color = Color.White)
        HomeTeamPhotoViewerUiState.Error ->
            Text(stringResource(R.string.home_team_result_image_error), color = Color.White)
        is HomeTeamPhotoViewerUiState.Content ->
            SubcomposeAsyncImage(
                model = rememberSubmittedPhotoRequest(state.image),
                contentDescription = stringResource(R.string.home_team_result_submitted_photo),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                loading = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .shimmerEffect(
                                baseColor = Color(0xFF2A2A2A),
                                shimmerColor = Color(0xFF3D3D3D),
                            ),
                    )
                },
                error = {
                    Text(stringResource(R.string.home_team_result_image_error), color = Color.White)
                },
            )
    }
}

@Composable
fun HomeTeamSubmittedPhotoThumbnail(
    image: TeamAttendancePhotoImage,
    modifier: Modifier,
) {
    AsyncImage(
        model = rememberSubmittedPhotoRequest(image),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun ConfigureBlackPhotoViewerSystemBars() {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    DisposableEffect(window) {
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        val previousStatusBarAppearance = controller?.isAppearanceLightStatusBars
        val previousNavigationBarAppearance = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            previousStatusBarAppearance?.let { controller?.isAppearanceLightStatusBars = it }
            previousNavigationBarAppearance?.let { controller?.isAppearanceLightNavigationBars = it }
        }
    }
}

@Composable
private fun rememberSubmittedPhotoRequest(image: TeamAttendancePhotoImage): ImageRequest {
    val context = LocalContext.current
    return remember(context, image.url) {
        ImageRequest
            .Builder(context)
            .data(image.url)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }
}
