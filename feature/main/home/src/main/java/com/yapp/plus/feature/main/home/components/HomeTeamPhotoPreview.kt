package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeTeamPhotoPreview(
    photo: Painter,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = photo,
        contentDescription = stringResource(R.string.home_team_photo_preview_description),
        modifier =
            modifier
                .fillMaxWidth()
                .height(206.dp)
                .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
    )
}
