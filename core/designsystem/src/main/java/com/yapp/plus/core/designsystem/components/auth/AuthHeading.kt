package com.yapp.plus.core.designsystem.components.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun AuthHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    YappText(
        text = text,
        modifier = modifier.semantics { heading() },
        style = YappTypography.scaleT10Bold,
        color = YappAuthColor.textPrimary,
    )
}
