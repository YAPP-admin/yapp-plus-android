package com.yapp.plus.core.designsystem.component.chip

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class ChipMetrics(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp,
) {
    companion object {
        fun from(size: YappChipSize): ChipMetrics =
            when (size) {
                YappChipSize.Large -> ChipMetrics(10.dp, 3.dp, 8.dp)
                YappChipSize.Small -> ChipMetrics(8.dp, 2.dp, 6.dp)
            }
    }
}
