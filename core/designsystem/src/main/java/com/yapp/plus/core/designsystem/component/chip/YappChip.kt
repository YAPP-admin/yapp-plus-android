package com.yapp.plus.core.designsystem.component.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Composable
fun YappChip(
    text: String,
    color: YappChipColor,
    size: YappChipSize,
    style: YappChipStyle,
    modifier: Modifier = Modifier,
) {
    val palette = chipPalette(color)
    val colors = chipColors(palette, style)
    val metrics = chipMetrics(size)

    Surface(
        modifier = modifier,
        color = colors.container,
        contentColor = colors.content,
        shape = RoundedCornerShape(metrics.cornerRadius),
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = metrics.horizontalPadding,
                    vertical = metrics.verticalPadding,
                ),
            style = chipTextStyle(size),
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun chipTextStyle(size: YappChipSize) =
    when (size) {
        YappChipSize.Large ->
            MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.2522.sp,
                fontWeight = FontWeight.Medium,
            )

        YappChipSize.Small ->
            MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.3421.sp,
                fontWeight = FontWeight.Medium,
            )
    }

private fun chipMetrics(size: YappChipSize) =
    when (size) {
        YappChipSize.Large -> ChipMetrics(10.dp, 3.dp, 8.dp)
        YappChipSize.Small -> ChipMetrics(8.dp, 2.dp, 6.dp)
    }

private fun chipPalette(color: YappChipColor) =
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

private fun chipColors(
    palette: ChipPalette,
    style: YappChipStyle,
) = when (style) {
    YappChipStyle.Fill -> ChipColors(palette.fill, YappColor.white)
    YappChipStyle.Weak -> ChipColors(palette.weak, palette.weakText)
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappChipPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = YappColor.white) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                YappChip(
                    text = "Large Fill",
                    color = YappChipColor.Orange,
                    size = YappChipSize.Large,
                    style = YappChipStyle.Fill,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappChip(
                        text = "Red",
                        color = YappChipColor.Red,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Orange",
                        color = YappChipColor.Orange,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Yellow",
                        color = YappChipColor.Yellow,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Neutral",
                        color = YappChipColor.Neutral,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappChip(
                        text = "Cool",
                        color = YappChipColor.CoolNeutral,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Lime",
                        color = YappChipColor.Lime,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Violet",
                        color = YappChipColor.Violet,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Blue",
                        color = YappChipColor.Blue,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappChip(
                        text = "Light Blue",
                        color = YappChipColor.LightBlue,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Pink",
                        color = YappChipColor.Pink,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Weak,
                    )
                    YappChip(
                        text = "Small Fill",
                        color = YappChipColor.Orange,
                        size = YappChipSize.Small,
                        style = YappChipStyle.Fill,
                    )
                }
            }
        }
    }
}
