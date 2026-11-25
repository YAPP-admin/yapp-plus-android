package com.yapp.plus.feature.main.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityPerson
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.SettlementRow
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun AfterpartyRoute(
    id: Long,
    onBack: () -> Unit,
    onSettlementClick: (Long) -> Unit,
    onAddSettlement: () -> Unit,
) {
    val vm: AfterpartyViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.load(id) }
    AfterpartyScreen(state, onBack, onSettlementClick, onAddSettlement, { vm.vote(it) })
}

@Composable
internal fun AfterpartyScreen(
    state: AfterpartyScreenState,
    onBack: () -> Unit,
    onSettlementClick: (Long) -> Unit,
    onAddSettlement: () -> Unit,
    onVote: (AfterpartyResponse) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        CommunityHeader(stringResource(R.string.community_attendance), onBack)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (val result = state.detail) {
                null -> CommunityLoading()
                is YappResult.Failure -> CommunityError(result.reason)
                is YappResult.Success -> {
                    val detail = result.value
                    AfterpartyInformation(detail)
                    AfterpartyVote(detail, state.submitting, onVote)
                    state.failure?.let { CommunityError(it, isRequest = true) }
                    if (detail.response != AfterpartyResponse.UNANSWERED) {
                        detail.attendingCount?.let { CommunityTitle(stringResource(R.string.community_attendees, it)) }
                        when (val roster = state.attendees) {
                            null -> CommunityLoading()
                            is YappResult.Failure -> CommunityError(roster.reason)
                            is YappResult.Success -> roster.value.forEach { CommunityPerson(it) }
                        }
                    }
                    HorizontalDivider()
                    CommunityTitle(stringResource(R.string.community_settlement))
                    CommunityCaption(stringResource(R.string.community_settlement_guide))
                    when (val settlements = state.settlements) {
                        null -> CommunityLoading()
                        is YappResult.Failure -> CommunityError(settlements.reason)
                        is YappResult.Success -> {
                            if (settlements.value.isEmpty()) {
                                CommunityCaption(
                                    stringResource(R.string.community_settlement_empty),
                                )
                            }
                            settlements.value.forEach { row -> SettlementRow(row) { onSettlementClick(row.id) } }
                        }
                    }
                    if (detail.response == AfterpartyResponse.ATTENDING && state.settlements is YappResult.Success) {
                        YappButton(
                            stringResource(R.string.community_settlement_add),
                            onClick = onAddSettlement,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AfterpartyInformation(detail: AfterpartyDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CommunityTitle(detail.title)
        CommunityCaption(detail.content)
        HorizontalDivider()
        CommunityCaption("${stringResource(R.string.community_date)}    ${detail.schedule.startsAtDisplay}")
        CommunityCaption("${stringResource(R.string.community_deadline)}    ${detail.schedule.deadlineDisplay}")
    }
}

@Composable
internal fun AfterpartyVote(
    detail: AfterpartyDetail,
    submitting: Boolean,
    onVote: (AfterpartyResponse) -> Unit,
) {
    var editing by rememberSaveable(detail.id) { mutableStateOf(false) }
    var selected by rememberSaveable(detail.id) { mutableStateOf(detail.response) }
    val now by produceState(Instant.now(), detail.schedule.deadline) {
        delay(
            java.time.Duration
                .between(value, detail.schedule.deadline)
                .toMillis()
                .coerceAtLeast(0),
        )
        value = detail.schedule.deadline
    }
    LaunchedEffect(detail.response) {
        selected = detail.response
        editing = false
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalDivider()
        CommunityTitle(stringResource(R.string.community_choice))
        if (detail.canVote(now) && (editing || detail.response == AfterpartyResponse.UNANSWERED)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(AfterpartyResponse.NOT_ATTENDING, AfterpartyResponse.ATTENDING).forEach { option ->
                    YappButton(
                        text = stringResource(option.label()),
                        onClick = { selected = option },
                        enabled = !submitting,
                        modifier = Modifier.weight(1f),
                        variant =
                            if (selected ==
                                option
                            ) {
                                YappButtonVariant.OutlinedPrimary
                            } else {
                                YappButtonVariant.OutlinedAssistive
                            },
                    )
                }
            }
            YappButton(
                text = stringResource(if (submitting) R.string.community_sending else R.string.community_submit),
                onClick = { onVote(selected) },
                enabled =
                    !submitting &&
                        selected != AfterpartyResponse.UNANSWERED &&
                        selected != detail.response,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            CommunityCaption(stringResource(detail.response.label()))
            if (detail.canVote(now)) {
                YappButton(stringResource(R.string.community_edit), onClick = {
                    editing = true
                }, variant = YappButtonVariant.OutlinedAssistive)
            } else {
                CommunityCaption(stringResource(R.string.community_voting_closed))
            }
        }
    }
}

private fun AfterpartyResponse.label(): Int =
    when (this) {
        AfterpartyResponse.ATTENDING -> R.string.community_attending
        AfterpartyResponse.NOT_ATTENDING -> R.string.community_absent
        AfterpartyResponse.UNANSWERED -> R.string.community_unanswered
    }
