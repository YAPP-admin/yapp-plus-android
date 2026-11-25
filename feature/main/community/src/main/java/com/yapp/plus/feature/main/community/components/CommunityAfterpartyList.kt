package com.yapp.plus.feature.main.community.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappIconLabel
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleSelectable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.feature.main.community.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val seoul = ZoneId.of("Asia/Seoul")
private val afterpartyDateFormat =
    DateTimeFormatter.ofPattern("MM.dd (E) HH:mm", Locale.KOREAN).withZone(seoul)

internal enum class CommunitySection(
    @StringRes val label: Int,
) {
    Afterparties(R.string.community_afterparties),
    Gatherings(R.string.community_gatherings),
}

@Composable
internal fun CommunityTabs(
    selectedSection: CommunitySection,
    onSelect: (CommunitySection) -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = YappMainColor.dividerMuted,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .selectableGroup(),
        ) {
            for (section in CommunitySection.entries) {
                CommunityTab(
                    label = stringResource(section.label),
                    isSelected = section == selectedSection,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelect(section) },
                )
            }
        }
    }
}

@Composable
internal fun CommunityTab(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .heightIn(min = 44.dp)
                .noRippleSelectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        YappText(
            text = label,
            style = YappTypography.scaleT5Bold,
            color = if (isSelected) YappMainColor.textPrimary else YappMainColor.textSubtle,
            modifier = Modifier.padding(vertical = 10.dp),
        )
        if (isSelected) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 2.dp,
                color = YappMainColor.textPrimary,
            )
        }
    }
}

@Composable
internal fun CommunityVoteFilters(
    selectedFilter: AfterpartyFilter,
    onFilter: (AfterpartyFilter) -> Unit,
) {
    FlowRow(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp)
                .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (filter in AfterpartyFilter.entries) {
            CommunityFilterChip(
                label = stringResource(filter.label()),
                isSelected = filter == selectedFilter,
                onClick = { onFilter(filter) },
            )
        }
    }
}

@Composable
internal fun CommunityFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (isSelected) YappMainColor.neutralSolid else YappMainColor.neutralWeakAlpha
    Box(
        modifier =
            Modifier
                .widthIn(min = 48.dp)
                .heightIn(min = 36.dp)
                .clip(RoundedCornerShape(50))
                .background(background)
                .noRippleSelectable(
                    selected = isSelected,
                    role = Role.RadioButton,
                    onClick = onClick,
                ).padding(horizontal = 14.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        YappText(
            text = label,
            style = YappTypography.scaleT4Medium,
            color = if (isSelected) YappMainColor.surface else YappMainColor.textPrimary,
        )
    }
}

@Composable
internal fun CommunityAfterpartyRow(
    item: AfterpartySummary,
    now: Instant,
    onClick: () -> Unit,
) {
    YappClickableSurface(onClick = onClick, color = YappMainColor.surface) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CommunityVoteStatus(item = item, now = now)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                YappText(
                    text = item.title,
                    style = YappTypography.scaleT5Bold,
                    color = YappMainColor.textPrimary,
                )
                CommunityAfterpartyMetadata(item)
            }
        }
    }
    HorizontalDivider(color = YappMainColor.dividerMuted)
}

@Composable
internal fun CommunityVoteStatus(
    item: AfterpartySummary,
    now: Instant,
) {
    val deadline = item.schedule.deadline
    val isOpen = item.state == AfterpartyState.OPEN && now.isBefore(deadline)
    val label =
        if (isOpen) {
            val today = now.atZone(seoul).toLocalDate()
            val deadlineDate = deadline.atZone(seoul).toLocalDate()
            val days = ChronoUnit.DAYS.between(today, deadlineDate)
            if (days == 0L) {
                stringResource(R.string.community_today)
            } else {
                stringResource(R.string.community_dday, days)
            }
        } else {
            stringResource(R.string.community_closed)
        }
    Box(
        modifier =
            Modifier
                .heightIn(min = 20.dp)
                .background(
                    color = if (isOpen) YappMainColor.brandWeakSurface else YappMainColor.basement,
                    shape = RoundedCornerShape(4.dp),
                ).padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        YappText(
            text = label,
            style = YappTypography.scaleT1Bold,
            color = if (isOpen) YappMainColor.brandContrast else YappMainColor.textSecondary,
        )
    }
}

@Composable
internal fun CommunityAfterpartyMetadata(item: AfterpartySummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappIconLabel(
            text = afterpartyDateFormat.format(item.schedule.startsAt),
            iconResource = R.drawable.ic_afterparty_calendar,
            modifier = Modifier.weight(1f),
            iconSize = 14.dp,
        )
        YappText(
            text = stringResource(R.string.community_responded, item.respondedCount),
            style = YappTypography.scaleT2Medium,
            color = YappMainColor.textSubtle,
        )
    }
}

private fun AfterpartyFilter.label(): Int =
    when (this) {
        AfterpartyFilter.ALL -> R.string.community_all
        AfterpartyFilter.OPEN -> R.string.community_open
        AfterpartyFilter.CLOSED -> R.string.community_closed
    }
