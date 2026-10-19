package com.yapp.plus.core.designsystem.component.chip

import androidx.compose.ui.graphics.Color
import com.yapp.plus.core.designsystem.theme.YappColor

internal data class ChipColors(
    val container: Color,
    val content: Color,
) {
    companion object {
        fun from(
            palette: ChipPalette,
            style: YappChipStyle,
        ): ChipColors =
            when (style) {
                YappChipStyle.Fill -> ChipColors(palette.fill, YappColor.white)
                YappChipStyle.Weak -> ChipColors(palette.weak, palette.weakText)
            }
    }
}
