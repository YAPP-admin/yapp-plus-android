package com.yapp.plus.core.designsystem.components.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappIconLabel(
    text: String,
    @DrawableRes iconResource: Int,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    maxLines: Int = 1,
) {
    val isMultiline = maxLines > 1
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = if (isMultiline) Alignment.Top else Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            modifier =
                Modifier
                    .padding(top = if (isMultiline) 2.dp else 0.dp)
                    .size(12.dp),
            tint = tint ?: YappMainColor.textFaint,
        )
        YappText(
            text = text,
            style = YappTypography.scaleT2Medium,
            color = tint ?: YappMainColor.textSubtle,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
