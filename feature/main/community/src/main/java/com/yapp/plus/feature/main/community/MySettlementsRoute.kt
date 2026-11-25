package com.yapp.plus.feature.main.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.main.YappIconLabel
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityEmptyState
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityFilterChip
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityTab
import com.yapp.plus.feature.main.community.components.SettlementBadge
import com.yapp.plus.feature.main.community.components.SettlementPaymentBadge
import com.yapp.plus.feature.main.community.components.label
import com.yapp.plus.feature.main.community.components.money
import com.yapp.plus.feature.main.community.components.settlementListDate

@Composable
fun MySettlementsRoute(
    onBack: () -> Unit,
    onClick: (Long, Long) -> Unit,
) {
    val vm: MySettlementsViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.refresh() }
    MySettlementsScreen(state, onBack, onClick)
}

@Composable
internal fun MySettlementsScreen(
    state: AfterpartyResult<List<AfterpartySettlements>>?,
    onBack: () -> Unit,
    onClick: (Long, Long) -> Unit,
) {
    var isReceiving by rememberSaveable { mutableStateOf(false) }
    var payingFilter by rememberSaveable { mutableStateOf<SettlementPaymentStatus?>(null) }
    var receivingFilter by rememberSaveable { mutableStateOf<SettlementPaymentStatus?>(null) }
    val filter = if (isReceiving) receivingFilter else payingFilter
    val title = if (isReceiving) R.string.community_receiving_settlements else R.string.community_my_settlements
    Column(Modifier.fillMaxSize().background(YappMainColor.surface)) {
        CommunityHeader(stringResource(title), onBack)
        SettlementListTabs(isReceiving) { isReceiving = it }
        SettlementListFilters(isReceiving, filter) {
            if (isReceiving) receivingFilter = it else payingFilter = it
        }
        when (state) {
            null -> CommunityLoading()
            is YappResult.Failure -> Box(Modifier.padding(20.dp)) { CommunityError(state.reason) }
            is YappResult.Success -> {
                val rows =
                    state.value
                        .flatMap { it.listRows(isReceiving) }
                        .filter { filter == null || it.status == filter }
                if (rows.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CommunityEmptyState(text = stringResource(R.string.community_settlement_empty))
                    }
                } else {
                    LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        items(rows, key = { it.settlementId }) { row ->
                            MySettlementListRow(row, isReceiving) {
                                onClick(row.afterparty.id, row.settlementId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SettlementListTabs(
    isReceiving: Boolean,
    onSelect: (Boolean) -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        HorizontalDivider(Modifier.align(Alignment.BottomCenter), color = YappMainColor.dividerMuted)
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).selectableGroup()) {
            CommunityTab(stringResource(R.string.community_to_pay), !isReceiving, Modifier.weight(1f)) {
                onSelect(false)
            }
            CommunityTab(stringResource(R.string.community_to_receive), isReceiving, Modifier.weight(1f)) {
                onSelect(true)
            }
        }
    }
}

@Composable
internal fun SettlementListFilters(
    isReceiving: Boolean,
    filter: SettlementPaymentStatus?,
    onFilter: (SettlementPaymentStatus?) -> Unit,
) {
    FlowRow(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 4.dp)
                .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(null, SettlementPaymentStatus.UNPAID, SettlementPaymentStatus.PAID).forEach { status ->
            val label =
                when {
                    status == null -> R.string.community_all
                    !isReceiving -> status.label()
                    status == SettlementPaymentStatus.UNPAID -> R.string.community_receiving
                    else -> R.string.community_received
                }
            CommunityFilterChip(stringResource(label), filter == status) { onFilter(status) }
        }
    }
}

@Composable
internal fun MySettlementListRow(
    row: MySettlementRow,
    isReceiving: Boolean,
    onClick: () -> Unit,
) {
    YappClickableSurface(onClick = onClick, color = YappMainColor.surface) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val status = row.status
            if (isReceiving && status == SettlementPaymentStatus.UNPAID) {
                SettlementBadge(
                    stringResource(R.string.community_receiving),
                    YappMainColor.brandContrast,
                    YappMainColor.brandWeakSurface,
                )
            } else if (isReceiving && status == SettlementPaymentStatus.PAID) {
                SettlementBadge(stringResource(R.string.community_received))
            } else if (status != null) {
                SettlementPaymentBadge(status)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                YappText(row.afterparty.title, YappTypography.scaleT5Bold, YappMainColor.textPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    YappIconLabel(
                        text = settlementListDate(row.afterparty.schedule.startsAt),
                        iconResource = R.drawable.ic_afterparty_calendar,
                        modifier = Modifier.weight(1f),
                        iconSize = 14.dp,
                    )
                    YappText(settlementListValue(row), YappTypography.scaleT2Medium, YappMainColor.textSubtle)
                }
            }
        }
    }
    HorizontalDivider(color = YappMainColor.dividerMuted)
}

@Composable
private fun settlementListValue(row: MySettlementRow): String {
    row.unpaidAmount?.let { return stringResource(R.string.community_list_remaining, money(it)) }
    row.summary?.let { return stringResource(R.string.community_receive_count, it.paidCount, it.debtorCount) }
    return stringResource(R.string.community_unknown_value)
}
