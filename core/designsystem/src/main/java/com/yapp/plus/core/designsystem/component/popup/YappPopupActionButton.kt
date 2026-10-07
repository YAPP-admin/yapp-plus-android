package com.yapp.plus.core.designsystem.component.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
internal fun YappPopupActionButton(
    text: String,
    primary: Boolean,
    height: Dp,
    modifier: Modifier = Modifier,
    variant: YappAlertDialogVariant = YappAlertDialogVariant.Default,
    onClick: () -> Unit,
) {
    val isCompact = variant == YappAlertDialogVariant.Compact
    val cornerRadius =
        when {
            isCompact -> 8.dp
            primary -> 12.dp
            else -> 10.dp
        }
    val containerColor =
        when {
            isCompact && primary -> YappColor.brand
            isCompact -> YappColor.backgroundBasement
            primary -> YappColor.primary
            else -> YappColor.white
        }
    val contentColor =
        when {
            primary -> YappColor.white
            isCompact -> YappColor.foregroundSubtle
            else -> YappColor.primary
        }
    Surface(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        border = if (primary || isCompact) null else BorderStroke(1.dp, YappColor.divider),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style =
                    if (isCompact) {
                        YappTypography.label1NormalBold.copy(
                            lineHeight = 18.sp,
                            letterSpacing = 0.sp,
                        )
                    } else {
                        YappTypography.body1NormalBold
                    },
                color = contentColor,
            )
        }
    }
}
