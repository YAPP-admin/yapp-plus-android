package com.yapp.plus.core.designsystem.components.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappMainHeader(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconResource: Int? = null,
    iconDescription: String? = null,
    onIconClick: () -> Unit = {},
    horizontalPadding: Dp = 20.dp,
    leadingContent: (@Composable () -> Unit)? = null,
    iconTint: Color = YappMainColor.textSecondary,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent == null) {
            YappText(title, YappTypography.scaleT8Bold, YappMainColor.textPrimary)
        } else {
            leadingContent()
        }
        if (iconResource != null) {
            IconButton(onClick = onIconClick, modifier = Modifier.size(24.dp)) {
                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = iconDescription,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint,
                )
            }
        }
    }
}
