package com.yapp.plus.core.designsystem.component.scroll

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class YappScrollBarSize(
    val width: Dp,
    val containerWidth: Dp
) {
    Normal(width = 7.dp, containerWidth = 13.dp),
    Small(width = 3.dp, containerWidth = 9.dp)
}
