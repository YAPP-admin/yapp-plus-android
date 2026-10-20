package com.yapp.plus.core.designsystem.components.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappDemoDataLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    YappText(
        text = text,
        style = YappTypography.caption1Bold,
        color = YappMainColor.textSecondary,
        modifier = modifier,
    )
}
