package com.yapp.plus.core.designsystem.component.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AuthGraphic(
    @DrawableRes resource: Int,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
) {
    Image(
        painter = painterResource(resource),
        contentDescription = null,
        modifier =
            modifier
                .widthIn(max = 350.dp)
                .fillMaxWidth()
                .height(height),
        contentScale = ContentScale.Fit,
    )
}
