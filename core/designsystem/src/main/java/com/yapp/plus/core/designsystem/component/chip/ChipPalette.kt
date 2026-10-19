package com.yapp.plus.core.designsystem.component.chip

import androidx.compose.ui.graphics.Color
import com.yapp.plus.core.designsystem.theme.YappColor

internal data class ChipPalette(
    val fill: Color,
    val weak: Color,
    val weakText: Color,
) {
    companion object {
        fun from(color: YappChipColor): ChipPalette =
            when (color) {
                YappChipColor.Red ->
                    ChipPalette(
                        YappColor.red,
                        YappColor.redWeak,
                        YappColor.red,
                    )

                YappChipColor.Orange ->
                    ChipPalette(
                        YappColor.orange,
                        YappColor.orangeWeak,
                        YappColor.orange,
                    )

                YappChipColor.Yellow ->
                    ChipPalette(
                        YappColor.yellow,
                        YappColor.yellowWeak,
                        YappColor.yellow,
                    )

                YappChipColor.Neutral ->
                    ChipPalette(
                        YappColor.neutral,
                        YappColor.neutralWeak,
                        YappColor.neutralText,
                    )

                YappChipColor.CoolNeutral ->
                    ChipPalette(
                        YappColor.coolNeutral,
                        YappColor.coolNeutralWeak,
                        YappColor.coolNeutral,
                    )

                YappChipColor.Lime ->
                    ChipPalette(
                        YappColor.lime,
                        YappColor.limeWeak,
                        YappColor.lime,
                    )

                YappChipColor.Violet ->
                    ChipPalette(
                        YappColor.violet,
                        YappColor.violetWeak,
                        YappColor.violet,
                    )

                YappChipColor.Blue ->
                    ChipPalette(
                        YappColor.blue,
                        YappColor.blueWeak,
                        YappColor.blue,
                    )

                YappChipColor.LightBlue ->
                    ChipPalette(
                        YappColor.lightBlue,
                        YappColor.lightBlueWeak,
                        YappColor.lightBlue,
                    )

                YappChipColor.Pink ->
                    ChipPalette(
                        YappColor.pink,
                        YappColor.pinkWeak,
                        YappColor.pink,
                    )
            }
    }
}
