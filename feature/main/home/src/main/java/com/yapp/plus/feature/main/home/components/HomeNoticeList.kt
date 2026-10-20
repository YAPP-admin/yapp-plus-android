package com.yapp.plus.feature.main.home.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun HomeNoticeList(
    notices: List<HomeNoticeUiModel>,
    onNoticeClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (notice in notices) HomeNoticeCard(notice, { onNoticeClick(notice.id) })
    }
}

@Composable
internal fun HomeNoticeCard(
    notice: HomeNoticeUiModel,
    onClick: () -> Unit,
) {
    val chipColor =
        if (notice.isSessionNotice) {
            YappHomeColor.sessionWeak
        } else {
            YappHomeColor.background
        }
    val chipLabel =
        if (notice.isSessionNotice) {
            R.string.home_notice_session
        } else {
            R.string.home_notice_other
        }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = YappHomeColor.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = chipColor,
                    shape = RoundedCornerShape(4.dp),
                ) {
                    YappText(
                        stringResource(chipLabel),
                        YappHomeTypography.captionBold,
                        if (notice.isSessionNotice) YappHomeColor.session else YappHomeColor.muted,
                        Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                YappText(
                    notice.title,
                    YappHomeTypography.bodyBold,
                    YappHomeColor.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                painterResource(DesignSystemR.drawable.ic_chevron_right),
                null,
                Modifier.size(20.dp),
                YappHomeColor.border,
            )
        }
    }
}
