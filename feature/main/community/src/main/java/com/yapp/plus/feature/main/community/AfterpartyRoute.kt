package com.yapp.plus.feature.main.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityPerson
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.SettlementEditorAction
import com.yapp.plus.feature.main.community.components.SettlementEventInfo
import com.yapp.plus.feature.main.community.components.SettlementNotice
import com.yapp.plus.feature.main.community.components.SettlementRow
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun AfterpartyRoute(
    id: Long,
    onBack: () -> Unit,
    onSettlementClick: (Long) -> Unit,
    onAddSettlement: () -> Unit,
    settlementRecipientCount: Int? = null,
    onDismissSettlementNotice: () -> Unit = {},
) {
    val vm: AfterpartyViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.load(id) }
    AfterpartyScreen(
        state,
        onBack,
        onSettlementClick,
        onAddSettlement,
        { vm.vote(it) },
        settlementRecipientCount,
        onDismissSettlementNotice,
    )
}

@Composable
internal fun AfterpartyScreen(
    state: AfterpartyScreenState,
    onBack: () -> Unit,
    onSettlementClick: (Long) -> Unit,
    onAddSettlement: () -> Unit,
    onVote: (AfterpartyResponse) -> Unit,
    settlementRecipientCount: Int? = null,
    onDismissSettlementNotice: () -> Unit = {},
) {
    Column(Modifier.fillMaxSize()) {
        CommunityHeader(stringResource(R.string.community_attendance), onBack)
        when (val result = state.detail) {
            null -> CommunityLoading()
            is YappResult.Failure ->
                Box(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    CommunityError(result.reason)
                }
            is YappResult.Success -> {
                val detail = result.value
                val add =
                    onAddSettlement.takeIf {
                        detail.response == AfterpartyResponse.ATTENDING && state.settlements is YappResult.Success
                    }
                val notice =
                    settlementRecipientCount?.let {
                        stringResource(R.string.community_settlement_created, it)
                    }
                if (detail.state == AfterpartyState.ENDED && detail.response == AfterpartyResponse.ATTENDING) {
                    FinishedAfterpartySettlement(
                        detail,
                        state,
                        add,
                        notice,
                        onDismissSettlementNotice,
                        onSettlementClick,
                    )
                } else {
                    AfterpartyContent(detail, state.submitting, onVote, add, notice, onDismissSettlementNotice) {
                        AfterpartySections(detail, state, onSettlementClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun FinishedAfterpartySettlement(
    detail: AfterpartyDetail,
    state: AfterpartyScreenState,
    onAddSettlement: (() -> Unit)?,
    notice: String?,
    onDismissNotice: () -> Unit,
    onSettlementClick: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize().background(YappMainColor.basement)) {
        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SettlementEventInfo(detail, showDeadline = true)
                AfterpartySettlementSection(state, onSettlementClick)
            }
            notice?.let {
                SettlementNotice(it, onDismissNotice, Modifier.align(Alignment.BottomCenter))
            }
        }
        onAddSettlement?.let { AddSettlementAction(it) }
    }
}

@Composable
private fun AddSettlementAction(onClick: () -> Unit) {
    Box(Modifier.background(YappMainColor.surface)) {
        SettlementEditorAction(
            text = stringResource(R.string.community_settlement_add),
            enabled = true,
            onClick = onClick,
        )
    }
}

@Composable
private fun AfterpartySections(
    detail: AfterpartyDetail,
    state: AfterpartyScreenState,
    onSettlementClick: (Long) -> Unit,
) {
    val canShowAttendees = detail.response != AfterpartyResponse.UNANSWERED
    if (canShowAttendees) {
        detail.attendingCount?.let { CommunityTitle(stringResource(R.string.community_attendees, it)) }
        when (val roster = state.attendees) {
            null -> if (state.settlements != null) CommunityLoading() else Unit
            is YappResult.Failure -> CommunityError(roster.reason)
            is YappResult.Success -> roster.value.forEach { CommunityPerson(it) }
        }
    }
    AfterpartySettlementSection(state, onSettlementClick, contentPadding = 0.dp)
}

@Composable
internal fun AfterpartySettlementSection(
    state: AfterpartyScreenState,
    onSettlementClick: (Long) -> Unit,
    contentPadding: Dp = 20.dp,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(YappMainColor.surface)
            .padding(horizontal = contentPadding, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        YappText(
            stringResource(R.string.community_settlement),
            YappTypography.scaleT5Bold,
            YappMainColor.textPrimary,
        )
        YappText(
            stringResource(R.string.community_settlement_guide),
            YappTypography.scaleT4Regular,
            YappMainColor.textSecondary,
        )
        state.failure?.let { CommunityError(it, isRequest = true) }
        when (val settlements = state.settlements) {
            null -> CommunityLoading()
            is YappResult.Failure -> CommunityError(settlements.reason)
            is YappResult.Success -> {
                if (settlements.value.isEmpty()) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 88.dp), contentAlignment = Alignment.Center) {
                        YappText(
                            stringResource(R.string.community_settlement_empty),
                            YappTypography.scaleT4Regular,
                            YappMainColor.textFaint,
                        )
                    }
                }
                Column(Modifier.padding(top = 8.dp)) {
                    settlements.value.forEachIndexed { index, row ->
                        if (index > 0) HorizontalDivider(color = YappMainColor.dividerMuted)
                        SettlementRow(row, state.ownerSummaries[row.id]?.perPersonAmount) { onSettlementClick(row.id) }
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
internal fun AfterpartyContent(
    detail: AfterpartyDetail,
    submitting: Boolean,
    onVote: (AfterpartyResponse) -> Unit,
    onAddSettlement: (() -> Unit)? = null,
    settlementNotice: String? = null,
    onDismissSettlementNotice: () -> Unit = {},
    content: @Composable () -> Unit,
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
    val canVote = detail.canVote(now)
    val showChoices = canVote && (editing || detail.response == AfterpartyResponse.UNANSWERED)
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AfterpartyInformation(detail)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HorizontalDivider()
                CommunityTitle(stringResource(R.string.community_choice))
                if (showChoices) {
                    AfterpartyVoteOptions(selected, submitting) { selected = it }
                } else {
                    CommunityCaption(stringResource(detail.response.label()))
                    if (canVote) {
                        YappButton(
                            stringResource(R.string.community_edit),
                            onClick = { editing = true },
                            variant = YappButtonVariant.OutlinedAssistive,
                        )
                    } else {
                        CommunityCaption(stringResource(R.string.community_voting_closed))
                    }
                }
            }
            content()
        }
        settlementNotice?.let { SettlementNotice(it, onDismissSettlementNotice) }
        if (showChoices) {
            YappButton(
                text = stringResource(if (submitting) R.string.community_sending else R.string.community_submit),
                onClick = { onVote(selected) },
                enabled =
                    !submitting &&
                        selected != AfterpartyResponse.UNANSWERED &&
                        selected != detail.response,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            )
        } else {
            onAddSettlement?.let { AddSettlementAction(it) }
        }
    }
}

@Composable
private fun AfterpartyVoteOptions(
    selected: AfterpartyResponse,
    submitting: Boolean,
    onSelected: (AfterpartyResponse) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        for (option in listOf(AfterpartyResponse.NOT_ATTENDING, AfterpartyResponse.ATTENDING)) {
            YappButton(
                text = stringResource(option.label()),
                onClick = { onSelected(option) },
                enabled = !submitting,
                modifier = Modifier.weight(1f),
                variant =
                    if (selected == option) {
                        YappButtonVariant.OutlinedPrimary
                    } else {
                        YappButtonVariant.OutlinedAssistive
                    },
            )
        }
    }
}

private fun AfterpartyResponse.label(): Int =
    when (this) {
        AfterpartyResponse.ATTENDING -> R.string.community_attending
        AfterpartyResponse.NOT_ATTENDING -> R.string.community_absent
        AfterpartyResponse.UNANSWERED -> R.string.community_unanswered
    }
