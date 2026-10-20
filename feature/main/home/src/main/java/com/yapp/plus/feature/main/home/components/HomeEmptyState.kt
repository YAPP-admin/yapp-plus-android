package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun HomeEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    graphicWidth: Dp = 66.dp,
    graphicHeight: Dp = 54.dp,
    spacing: Dp = 14.dp,
    textStyle: TextStyle = YappHomeTypography.body,
    graphicAlpha: Float = 0.5f,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painterResource(DesignSystemR.drawable.graphic_none),
            null,
            Modifier.size(width = graphicWidth, height = graphicHeight).alpha(graphicAlpha),
        )
        YappText(message, textStyle, YappHomeColor.subtle)
    }
}
