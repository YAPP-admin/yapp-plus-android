package com.yapp.plus.core.designsystem.component.button

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class ButtonMetrics(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp,
    val iconSpacing: Dp,
    val iconSize: Dp,
    val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val minimumHeight: Dp = 0.dp,
) {
    companion object {
        fun from(size: YappButtonSize): ButtonMetrics =
            when (size) {
                YappButtonSize.CallToAction ->
                    ButtonMetrics(
                        horizontalPadding = 20.dp,
                        verticalPadding = 16.dp,
                        cornerRadius = 12.dp,
                        iconSpacing = 8.dp,
                        iconSize = 24.dp,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    )

                YappButtonSize.Social ->
                    ButtonMetrics(
                        horizontalPadding = 20.dp,
                        verticalPadding = 9.dp,
                        cornerRadius = 12.dp,
                        iconSpacing = 0.dp,
                        iconSize = 36.dp,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        minimumHeight = 54.dp,
                    )

                YappButtonSize.XLarge -> ButtonMetrics(36.dp, 16.dp, 12.dp, 6.dp, 20.dp)
                YappButtonSize.Large -> ButtonMetrics(28.dp, 12.dp, 10.dp, 6.dp, 20.dp)
                YappButtonSize.Medium -> ButtonMetrics(20.dp, 9.dp, 8.dp, 5.dp, 18.dp)
                YappButtonSize.Small -> ButtonMetrics(14.dp, 7.dp, 6.dp, 4.dp, 16.dp)
                YappButtonSize.XSmall -> ButtonMetrics(12.dp, 5.dp, 4.dp, 4.dp, 16.dp)
            }
    }
}
