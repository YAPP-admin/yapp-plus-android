package com.yapp.plus.core.designsystem.components.input

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class InputMetrics(
    val height: Dp,
    val horizontalPadding: Dp,
    val cornerRadius: Dp,
    val iconSpacing: Dp,
) {
    companion object {
        fun from(
            size: YappInputSize,
            variant: YappTextFieldVariant = YappTextFieldVariant.Default,
        ): InputMetrics =
            if (variant == YappTextFieldVariant.Auth) {
                InputMetrics(
                    height = 54.dp,
                    horizontalPadding = 16.dp,
                    cornerRadius = 12.dp,
                    iconSpacing = 12.dp,
                )
            } else {
                when (size) {
                    YappInputSize.Large -> InputMetrics(48.dp, 16.dp, 10.dp, 12.dp)
                    YappInputSize.Medium -> InputMetrics(40.dp, 10.dp, 8.dp, 12.dp)
                    YappInputSize.Small -> InputMetrics(32.dp, 8.dp, 6.dp, 8.dp)
                }
            }
    }
}
