package com.yapp.plus.core.designsystem.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object YappHomeTypography {
    val day =
        YappTypography.caption2Bold.copy(
            fontSize = 11.sp,
            lineHeight = 15.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Normal,
        )
    val caption =
        YappTypography.caption1Bold.copy(
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Medium,
        )
    val label = YappTypography.label2Medium.copy(lineHeight = 18.sp, letterSpacing = 0.sp)
    val captionBold = caption.copy(fontWeight = FontWeight.Bold)
    val body = YappTypography.label1NormalMedium.copy(lineHeight = 19.sp, letterSpacing = 0.sp)
    val bodyBold = body.copy(fontWeight = FontWeight.Bold)
    val bodyLarge = YappTypography.body1NormalRegular.copy(lineHeight = 22.sp, letterSpacing = 0.sp)
    val input = bodyLarge.copy(fontWeight = FontWeight.Medium)
    val cardTitle =
        YappTypography.body1NormalBold.copy(
            lineHeight = 22.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Bold,
        )
    val heading =
        YappTypography.headline1Bold.copy(
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Bold,
        )
    val title =
        YappTypography.heading2Bold.copy(
            lineHeight = 27.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Bold,
        )
    val completion =
        YappTypography.title2Bold.copy(
            fontSize = 26.sp,
            lineHeight = 35.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Bold,
        )
}
