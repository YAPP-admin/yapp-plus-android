package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.yapp.plus.core.designsystem.modifier.WithoutRipple
import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeTeamPhotoViewerUiState

@Composable
internal fun HomeTeamSubmittedPhotoDialog(
    state: HomeTeamPhotoViewerUiState,
    onClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(stringResource(R.string.home_team_result_view_photo)) },
        text = {
            when (state) {
                HomeTeamPhotoViewerUiState.Loading ->
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                HomeTeamPhotoViewerUiState.Unavailable ->
                    Text(stringResource(R.string.home_team_result_image_unavailable))
                HomeTeamPhotoViewerUiState.Error ->
                    Text(stringResource(R.string.home_team_result_image_error))
                is HomeTeamPhotoViewerUiState.Content ->
                    SubcomposeAsyncImage(
                        model = rememberSubmittedPhotoRequest(state.image),
                        contentDescription = stringResource(R.string.home_team_result_submitted_photo),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 420.dp),
                        contentScale = ContentScale.Fit,
                        loading = { CircularProgressIndicator() },
                        error = { Text(stringResource(R.string.home_team_result_image_error)) },
                    )
            }
        },
        confirmButton = {
            WithoutRipple {
                TextButton(onClick = onClose) {
                    Text(stringResource(R.string.home_confirm))
                }
            }
        },
    )
}

@Composable
internal fun HomeTeamSubmittedPhotoThumbnail(
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
