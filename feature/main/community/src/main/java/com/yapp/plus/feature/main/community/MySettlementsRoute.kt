package com.yapp.plus.feature.main.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityFilters
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.SettlementRow
import com.yapp.plus.feature.main.community.components.label

@Composable
fun MySettlementsRoute(
    onBack: () -> Unit,
    onClick: (Long, Long) -> Unit,
) {
    val vm: MySettlementsViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    var isReceiving by rememberSaveable { mutableStateOf(false) }
    var payment by rememberSaveable { mutableStateOf<SettlementPaymentStatus?>(null) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.refresh() }
    Column(Modifier.fillMaxSize()) {
        CommunityHeader(stringResource(R.string.community_my_settlements), onBack)
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CommunityFilters(
                listOf(false, true),
                isReceiving,
                { stringResource(if (it) R.string.community_to_receive else R.string.community_to_pay) },
                { isReceiving = it },
            )
            if (!isReceiving) {
                CommunityFilters(
                    options = listOf(null, SettlementPaymentStatus.UNPAID, SettlementPaymentStatus.PAID),
                    selected = payment,
                    label = { stringResource(it?.label() ?: R.string.community_all) },
                    onSelected = { payment = it },
                )
            }
            when (val result = state) {
                null -> CommunityLoading()
                is YappResult.Failure -> CommunityError(result.reason)
                is YappResult.Success -> {
                    val rows =
                        result.value.filter { (_, settlement) ->
                            settlement.isCreator == isReceiving &&
                                (isReceiving || payment == null || payment == settlement.paymentStatus)
                        }
                    LazyColumn {
                        if (rows.isEmpty()) {
                            item {
                                CommunityCaption(
                                    stringResource(R.string.community_settlement_empty),
                                )
                            }
                        }
                        items(rows, key = { it.second.id }) { (party, settlement) ->
                            CommunityTitle(party.title)
                            CommunityCaption(party.schedule.startsAtDisplay)
                            SettlementRow(settlement) { onClick(party.id, settlement.id) }
                        }
                    }
                }
            }
        }
    }
}
