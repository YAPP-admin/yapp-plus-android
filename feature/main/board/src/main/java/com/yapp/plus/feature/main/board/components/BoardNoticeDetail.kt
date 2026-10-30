package com.yapp.plus.feature.main.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel

@Composable
internal fun BoardNoticeDetail(
    notice: BoardNoticeUiModel,
    onMarkRead: () -> Unit = {},
    isMarkingRead: Boolean = false,
    hasReadError: Boolean = false,
    onReload: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState(),
            ).padding(top = 4.dp, bottom = 64.dp),
    ) {
        YappText(notice.title, YappTypography.scaleT7Bold, YappMainColor.textPrimary)
        Row(
            Modifier.padding(top = 10.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoardNoticeBadge(notice.category)
            notice.daysUntilDeadline?.let { BoardDeadlineBadge(it) }
            YappText(notice.date, YappTypography.scaleT3Medium, YappMainColor.textFaint)
        }
        HorizontalDivider(color = YappMainColor.divider)
        Column(Modifier.padding(top = 28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            notice.assignment?.let { BoardAssignmentInfo(it) }
            BoardMarkdown(notice.content)
            notice.images.forEach { BoardNoticeImage(it, onReload) }
        }
        BoardReadConfirmation(notice, onMarkRead, isMarkingRead, hasReadError, onReload)
    }
}
