package com.yapp.plus.core.designsystem.component.auth

import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.resource.svgPainterResource

@Composable
fun AuthGraphic(
    @RawRes resource: Int,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
) {
    Image(
        painter = svgPainterResource(resource),
        contentDescription = null,
        modifier =
            modifier
                .widthIn(max = 350.dp)
                .fillMaxWidth()
                .height(height),
        contentScale = ContentScale.Fit,
    )
}
