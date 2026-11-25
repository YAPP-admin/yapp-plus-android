package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.components.toast.YappToast
import com.yapp.plus.core.designsystem.components.toast.YappToastVariant
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.feature.main.community.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val seoul = ZoneId.of("Asia/Seoul")
private val fullDate = DateTimeFormatter.ofPattern("yyyy.MM.dd (E) HH:mm", Locale.KOREAN)
private val shortDate = DateTimeFormatter.ofPattern("MM.dd (E) HH:mm", Locale.KOREAN)

internal fun settlementListDate(
    instant: Instant,
    now: Instant = Instant.now(),
): String {
    val date = instant.atZone(seoul)
    return date.format(if (date.year == now.atZone(seoul).year) shortDate else fullDate)
}

@Composable
internal fun SettlementEventInfo(
    detail: AfterpartyDetail,
    showDeadline: Boolean,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(YappMainColor.surface)
                .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (detail.state == AfterpartyState.ENDED) {
                SettlementBadge(stringResource(R.string.community_ended))
            }
            if (detail.state == AfterpartyState.CLOSED) {
                SettlementBadge(stringResource(R.string.community_closed))
            }
            YappText(detail.title, YappTypography.scaleT8Bold, YappMainColor.textPrimary)
            YappText(detail.content, YappTypography.scaleT4Regular, YappMainColor.textSecondary)
        }
        HorizontalDivider(color = YappMainColor.divider)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettlementEventDate(R.string.community_meeting_date, detail.schedule.startsAt)
            if (showDeadline) {
                SettlementEventDate(R.string.community_settlement_deadline, detail.schedule.deadline)
            }
        }
    }
}

@Composable
private fun SettlementEventDate(
    label: Int,
    instant: Instant,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        YappText(
            stringResource(label),
            YappTypography.scaleT4Medium,
            YappMainColor.textSubtle,
            Modifier.width(64.dp),
        )
        YappText(
            instant.atZone(seoul).format(fullDate),
            YappTypography.scaleT4Medium,
            YappMainColor.textSecondary,
        )
    }
}

@Composable
internal fun SettlementBadge(
    text: String,
    color: Color = YappMainColor.textSecondary,
    background: Color = YappMainColor.basement,
) {
    Box(
        modifier =
            Modifier
                .heightIn(min = 20.dp)
                .background(background, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        YappText(text, YappTypography.scaleT1Bold, color)
    }
}

@Composable
internal fun SettlementPaymentBadge(status: SettlementPaymentStatus) {
    val paid = status == SettlementPaymentStatus.PAID
    SettlementBadge(
        text = stringResource(status.label()),
        color = if (paid) YappMainColor.assignment else YappMainColor.critical,
        background = if (paid) YappMainColor.assignmentWeak else YappMainColor.criticalWeak,
    )
}

@Composable
internal fun SettlementCallout(text: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .background(YappMainColor.basement, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(R.drawable.ic_settlement_notice),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        YappText(text, YappTypography.scaleT4Regular, YappMainColor.textPrimary)
    }
}

@Composable
internal fun SettlementNotice(
    text: String,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    YappToast(
        text = text,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        showIcon = false,
        variant = YappToastVariant.Home,
        actionText = if (onDismiss == null) null else stringResource(R.string.community_confirm),
        onAction = { onDismiss?.invoke() },
    )
}
