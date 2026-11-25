package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.modifier.noRippleSelectable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementSummary
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.R
import com.yapp.plus.feature.main.community.SettlementReceipts
import com.yapp.plus.feature.main.community.SettlementScreenState
import com.yapp.plus.feature.main.community.valueOrNull

@Composable
internal fun SettlementOwner(
    state: SettlementScreenState,
    onEdit: () -> Unit,
    onFilter: (SettlementPaymentStatus) -> Unit,
    onReload: () -> Unit,
) {
    val summary = state.summary.valueOrNull()
    val isComplete = summary != null && summary.paidCount >= summary.debtorCount
    val isPaidFilter = state.filter == SettlementPaymentStatus.PAID
    val isLoading = state.summary == null || state.people == null
    LazyColumn(Modifier.fillMaxSize().background(YappMainColor.surface)) {
        state.afterparty?.let { details ->
            item { SettlementEventInfo(details, showDeadline = false) }
            item { SettlementSectionDivider() }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SettlementOwnerHeading(onEdit)
                when (val result = state.summary) {
                    null -> Unit
                    is YappResult.Failure -> CommunityError(result.reason)
                    is YappResult.Success -> SettlementTotals(result.value)
                }
                state.failure?.let { CommunityError(it, isRequest = true) }
            }
        }
        item { SettlementSectionDivider() }
        item {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SettlementOwnerFilters(state.filter, summary, onFilter)
                if (!isPaidFilter && summary != null && !isComplete) {
                    SettlementCallout(stringResource(R.string.community_reminder))
                }
                if (isLoading) CommunityLoading()
            }
        }
        when (val people = state.people) {
            null -> Unit
            is YappResult.Failure ->
                item {
                    Box(Modifier.padding(horizontal = 20.dp)) { CommunityError(people.reason) }
                }
            is YappResult.Success -> {
                if (people.value.isEmpty() && !isLoading) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().heightIn(min = 220.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            YappText(
                                stringResource(
                                    if (isComplete && !isPaidFilter) {
                                        R.string.community_all_payments_received
                                    } else {
                                        R.string.community_payment_empty
                                    },
                                ),
                                YappTypography.scaleT4Regular,
                                YappMainColor.textSubtle,
                            )
                        }
                    }
                }
                items(people.value, key = { it.recipientId }) { person ->
                    SettlementRecipientRow(person, summary?.perPersonAmount, isPaidFilter)
                }
            }
        }
        val photos =
            state.detail
                .valueOrNull()
                ?.photos
                .orEmpty()
        if (photos.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SettlementReceipts(photos, onReload)
                }
            }
        }
    }
}

@Composable
internal fun SettlementSectionDivider() {
    Box(Modifier.fillMaxWidth().height(8.dp).background(YappMainColor.basement))
}

@Composable
internal fun SettlementOwnerHeading(onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YappText(
            stringResource(R.string.community_own_settlement),
            YappTypography.scaleT5Bold,
            YappMainColor.textPrimary,
        )
        YappText(
            stringResource(R.string.community_edit),
            YappTypography.scaleT4Medium,
            YappMainColor.textSubtle,
            Modifier.noRippleClickable(role = Role.Button, onClick = onEdit),
        )
    }
}

@Composable
internal fun SettlementTotals(summary: SettlementSummary) {
    val progress =
        if (summary.debtorCount <= 0) {
            0f
        } else {
            (summary.paidCount.toFloat() / summary.debtorCount).coerceIn(0f, 1f)
        }
    val isComplete = summary.paidCount >= summary.debtorCount
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SettlementTile(stringResource(R.string.community_receivable), Modifier.weight(1f)) {
                YappText(
                    money(summary.expectedReceivableAmount),
                    YappTypography.scaleT5Bold,
                    YappMainColor.textPrimary,
                )
            }
            SettlementTile(stringResource(R.string.community_paid), Modifier.weight(1f)) {
                Row {
                    YappText(
                        summary.paidCount.toString(),
                        YappTypography.scaleT5Bold,
                        YappMainColor.brand,
                    )
                    YappText(
                        stringResource(R.string.community_paid_total, summary.debtorCount),
                        YappTypography.scaleT5Bold,
                        if (isComplete) YappMainColor.brand else YappMainColor.textPrimary,
                    )
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(YappMainColor.basement)
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) },
        ) {
            Box(Modifier.fillMaxWidth(progress).height(6.dp).background(YappMainColor.brand, CircleShape))
        }
        YappText(
            text =
                if (isComplete) {
                    stringResource(R.string.community_payments_complete)
                } else {
                    stringResource(R.string.community_remaining, money(summary.remainingAmount))
                },
            style = YappTypography.scaleT4Regular,
            color = YappMainColor.textSubtle,
        )
    }
}

@Composable
private fun SettlementTile(
    label: String,
    modifier: Modifier,
    value: @Composable () -> Unit,
) {
    Surface(modifier, color = YappMainColor.basement, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            YappText(label, YappTypography.scaleT2Medium, YappMainColor.textSubtle)
            value()
        }
    }
}

@Composable
internal fun SettlementOwnerFilters(
    filter: SettlementPaymentStatus,
    summary: SettlementSummary?,
    onFilter: (SettlementPaymentStatus) -> Unit,
) {
    FlowRow(
        Modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(SettlementPaymentStatus.UNPAID, SettlementPaymentStatus.PAID).forEach { status ->
            val selected = filter == status
            val count =
                summary?.let {
                    if (status == SettlementPaymentStatus.PAID) {
                        it.paidCount
                    } else {
                        (it.debtorCount - it.paidCount).coerceAtLeast(0)
                    }
                }
            Surface(
                shape = RoundedCornerShape(50),
                color = if (selected) YappMainColor.neutralSolid else YappMainColor.surface,
                border = if (selected) null else BorderStroke(1.dp, YappMainColor.border),
            ) {
                Row(
                    Modifier
                        .heightIn(min = 36.dp)
                        .noRippleSelectable(selected, Role.RadioButton) { onFilter(status) }
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    YappText(
                        stringResource(status.label()),
                        YappTypography.scaleT4Medium,
                        if (selected) YappMainColor.surface else YappMainColor.textPrimary,
                    )
                    count?.let {
                        YappText(
                            it.toString(),
                            YappTypography.scaleT4Medium,
                            if (selected) YappMainColor.textFaint else YappMainColor.textSubtle,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SettlementRecipientRow(
    person: AfterpartyPerson,
    amount: Long?,
    isPaid: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(settlementAvatar(person.position)),
            contentDescription = null,
            modifier = Modifier.size(42.dp),
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            YappText(person.name, YappTypography.scaleT4Bold, YappMainColor.textPrimary)
            val metadata = listOfNotNull(person.teamName, positionLabel(person.position))
            if (metadata.isNotEmpty()) {
                YappText(metadata.joinToString("  |  "), YappTypography.scaleT3Medium, YappMainColor.textSubtle)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            YappText(
                amount?.let { money(it) } ?: stringResource(R.string.community_unknown_value),
                YappTypography.scaleT5Bold,
                YappMainColor.textPrimary,
            )
            if (isPaid) {
                YappText(
                    stringResource(R.string.community_payment_done),
                    YappTypography.scaleT2Medium,
                    YappMainColor.textSubtle,
                )
            }
        }
    }
}
