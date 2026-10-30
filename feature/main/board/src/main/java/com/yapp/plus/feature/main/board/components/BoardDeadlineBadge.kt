package com.yapp.plus.feature.main.board.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.components.chip.YappChip
import com.yapp.plus.core.designsystem.components.chip.YappChipColor
import com.yapp.plus.core.designsystem.components.chip.YappChipSize
import com.yapp.plus.core.designsystem.components.chip.YappChipStyle
import com.yapp.plus.feature.main.board.R

@Composable
internal fun BoardDeadlineBadge(days: Long) {
    val label =
        when {
            days > 0 -> stringResource(R.string.board_deadline_before, days)
            days == 0L -> stringResource(R.string.board_deadline_today)
            else -> stringResource(R.string.board_deadline_after, -days)
        }
    YappChip(
        label,
        color = YappChipColor.Yellow,
        size = YappChipSize.Small,
        style = YappChipStyle.Weak,
    )
}
