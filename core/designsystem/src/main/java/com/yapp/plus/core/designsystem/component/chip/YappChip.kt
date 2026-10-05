package com.yapp.plus.core.designsystem.component.chip

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.theme.YappTheme

@Composable
fun YappChip(
    text: String,
    modifier: Modifier = Modifier,
    color: YappChipColor = YappChipColor.Orange,
    size: YappChipSize = YappChipSize.Small,
    style: YappChipStyle = YappChipStyle.Weak
) {
    val palette = chipPalette(color)
    val colors = chipColors(palette, style)
    val metrics = chipMetrics(size)

    Surface(
        modifier = modifier,
        color = colors.container,
        contentColor = colors.content,
        shape = RoundedCornerShape(metrics.cornerRadius)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = metrics.horizontalPadding,
                vertical = metrics.verticalPadding
            ),
            style = chipTextStyle(size),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun chipTextStyle(size: YappChipSize) = when (size) {
    YappChipSize.Large -> MaterialTheme.typography.labelMedium.copy(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2522.sp,
        fontWeight = FontWeight.Medium
    )

    YappChipSize.Small -> MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3421.sp,
        fontWeight = FontWeight.Medium
    )
}

private fun chipMetrics(size: YappChipSize) = when (size) {
    YappChipSize.Large -> ChipMetrics(10.dp, 3.dp, 8.dp)
    YappChipSize.Small -> ChipMetrics(8.dp, 2.dp, 6.dp)
}

private fun chipPalette(color: YappChipColor) = when (color) {
    YappChipColor.Red -> ChipPalette(RedColor, RedWeakColor, RedColor)
    YappChipColor.Orange -> ChipPalette(OrangeColor, OrangeWeakColor, OrangeColor)
    YappChipColor.Yellow -> ChipPalette(YellowColor, YellowWeakColor, YellowColor)
    YappChipColor.Neutral -> ChipPalette(NeutralColor, NeutralWeakColor, NeutralTextColor)
    YappChipColor.CoolNeutral -> ChipPalette(
        CoolNeutralColor,
        CoolNeutralWeakColor,
        CoolNeutralColor
    )
    YappChipColor.Lime -> ChipPalette(LimeColor, LimeWeakColor, LimeColor)
    YappChipColor.Violet -> ChipPalette(VioletColor, VioletWeakColor, VioletColor)
    YappChipColor.Blue -> ChipPalette(BlueColor, BlueWeakColor, BlueColor)
    YappChipColor.LightBlue -> ChipPalette(LightBlueColor, LightBlueWeakColor, LightBlueColor)
    YappChipColor.Pink -> ChipPalette(PinkColor, PinkWeakColor, PinkColor)
}

private fun chipColors(
    palette: ChipPalette,
    style: YappChipStyle
) = when (style) {
    YappChipStyle.Fill -> ChipColors(palette.fill, WhiteColor)
    YappChipStyle.Weak -> ChipColors(palette.weak, palette.weakText)
}

private val RedColor = Color(0xFFE32908)
private val RedWeakColor = Color(0xFFFEE6E1)
private val OrangeColor = Color(0xFFFA6027)
private val OrangeWeakColor = Color(0xFFFFEFE9)
private val YellowColor = Color(0xFFFFAD31)
private val YellowWeakColor = Color(0xFFFFF7EA)
private val NeutralColor = Color(0xFF474747)
private val NeutralWeakColor = Color(0xFFDCDCDC)
private val NeutralTextColor = Color(0xFF5C5C5C)
private val CoolNeutralColor = Color(0xFF70737C)
private val CoolNeutralWeakColor = Color(0xFFF4F4F5)
private val LimeColor = Color(0xFF58CF04)
private val LimeWeakColor = Color(0xFFF0FEE6)
private val VioletColor = Color(0xFF6541F2)
private val VioletWeakColor = Color(0xFFECE7FD)
private val BlueColor = Color(0xFF0568FC)
private val BlueWeakColor = Color(0xFFE1EDFF)
private val LightBlueColor = Color(0xFF00AEFF)
private val LightBlueWeakColor = Color(0xFFE5F7FF)
private val PinkColor = Color(0xFFF553DA)
private val PinkWeakColor = Color(0xFFFEECFB)
private val WhiteColor = Color(0xFFFFFFFF)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappChipPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = Color.White) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                YappChip(
                    text = "Large Fill",
                    color = YappChipColor.Orange,
                    size = YappChipSize.Large,
                    style = YappChipStyle.Fill
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappChip("Red", color = YappChipColor.Red)
                    YappChip("Orange", color = YappChipColor.Orange)
                    YappChip("Yellow", color = YappChipColor.Yellow)
                    YappChip("Neutral", color = YappChipColor.Neutral)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappChip("Cool", color = YappChipColor.CoolNeutral)
                    YappChip("Lime", color = YappChipColor.Lime)
                    YappChip("Violet", color = YappChipColor.Violet)
                    YappChip("Blue", color = YappChipColor.Blue)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    YappChip("Light Blue", color = YappChipColor.LightBlue)
                    YappChip("Pink", color = YappChipColor.Pink)
                    YappChip("Small Fill", style = YappChipStyle.Fill)
                }
            }
        }
    }
}
