package com.yapp.plus.core.designsystem.component.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.component.text.YappText
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
        style =
            YappTypography.title3Bold.copy(
                fontSize = 26.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.5).sp,
            ),
        color = YappAuthColor.textPrimary,
    )
}
