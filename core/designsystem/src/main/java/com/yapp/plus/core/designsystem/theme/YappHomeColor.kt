package com.yapp.plus.core.designsystem.theme

import androidx.compose.ui.graphics.Color

object YappHomeColor {
    val background = YappColor.backgroundBasement
    val surface = YappColor.white
    val text = YappColor.foregroundNeutral
    val muted = YappColor.foregroundMuted
    val subtle = YappColor.foregroundSubtle
    val placeholder = YappColor.foregroundFaint
    val border = YappColor.strokeNeutralWeak
    val divider = Color(0x10000000)
    val action = Color(0xFF2A3038)
    val disabledAction = YappColor.foregroundMuted.copy(alpha = 0.5f)
    val brand = YappColor.brand
    val error = Color(0xFFFA342C)
    val session = Color(0xFF5E98FE)
    val sessionWeak = Color(0xFFEFF6FF)
    val scrim = Color.Black.copy(alpha = 0.46f)
    val celebration = Color(0xFFFFCB31)
}
