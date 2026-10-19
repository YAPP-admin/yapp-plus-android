package com.yapp.plus.core.designsystem.component.dropdown

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class DropdownMetrics(
    val height: Dp,
    val horizontalPadding: Dp,
    val cornerRadius: Dp,
    val iconSize: Dp,
    val menuItemHeight: Dp,
) {
    companion object {
        fun from(size: YappDropdownSize): DropdownMetrics =
            when (size) {
                YappDropdownSize.Large -> DropdownMetrics(48.dp, 16.dp, 10.dp, 24.dp, 48.dp)
                YappDropdownSize.Medium -> DropdownMetrics(40.dp, 10.dp, 8.dp, 20.dp, 40.dp)
            }
    }
}
