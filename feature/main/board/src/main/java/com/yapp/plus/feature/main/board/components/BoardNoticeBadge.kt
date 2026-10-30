package com.yapp.plus.feature.main.board.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.board.model.BoardCategory

@Composable
internal fun BoardNoticeBadge(
    category: BoardCategory,
    modifier: Modifier = Modifier,
) {
    val isSession = category == BoardCategory.Session
    val isAssignment = category == BoardCategory.Assignment
    Surface(
        modifier = modifier,
        color =
            when {
                isSession -> YappMainColor.sessionWeak
                isAssignment -> YappMainColor.assignmentWeak
                else -> YappMainColor.background
            },
        shape = RoundedCornerShape(4.dp),
    ) {
        YappText(
            text = stringResource(category.labelResource),
            style = YappTypography.scaleT2Bold,
            color =
                when {
                    isSession -> YappMainColor.session
                    isAssignment -> YappMainColor.assignment
                    else -> YappMainColor.textSecondary
                },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
