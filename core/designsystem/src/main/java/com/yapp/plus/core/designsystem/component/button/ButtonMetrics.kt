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
)
