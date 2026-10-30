package com.yapp.plus.feature.main.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.board.R
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel

@Composable
internal fun BoardReadConfirmation(
    notice: BoardNoticeUiModel,
    onMarkRead: () -> Unit,
    isMarkingRead: Boolean,
    hasReadError: Boolean,
    onReload: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().padding(top = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        YappText(
            stringResource(R.string.board_read_title),
            YappTypography.scaleT5Bold,
            YappMainColor.textPrimary,
        )
        YappText(
            stringResource(R.string.board_read_count, notice.readCount),
            YappTypography.scaleT3Medium,
            YappMainColor.textSecondary,
        )
        Surface(
            onClick = onMarkRead,
            enabled = !notice.isRead && !isMarkingRead,
            modifier = Modifier.padding(top = 14.dp),
            color = YappMainColor.basement,
            shape = RoundedCornerShape(999.dp),
        ) {
            Row(
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.ic_notice_read),
                    null,
                    Modifier.size(22.dp),
                    tint = Color.Unspecified,
                )
                YappText(
                    stringResource(
                        if (notice.isRead) R.string.board_read_done else R.string.board_read_action,
                    ),
                    YappTypography.scaleT4Bold,
                    YappMainColor.textSecondary,
                )
            }
        }
        if (hasReadError) {
            YappText(
                stringResource(R.string.board_read_error),
                YappTypography.scaleT3Medium,
                YappMainColor.critical,
            )
            YappButton(
                stringResource(R.string.board_retry),
                if (notice.isRead) onReload else onMarkRead,
            )
        }
    }
}
