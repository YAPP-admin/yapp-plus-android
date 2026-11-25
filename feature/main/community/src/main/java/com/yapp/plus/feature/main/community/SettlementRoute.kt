package com.yapp.plus.feature.main.community

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Build
import android.os.PersistableBundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.modifier.WithoutRipple
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementPhoto
import com.yapp.plus.domain.afterparty.SettlementSummary
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityCaption
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityFilters
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityPerson
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.ProtectedPhoto
import com.yapp.plus.feature.main.community.components.bankLabel
import com.yapp.plus.feature.main.community.components.label
import com.yapp.plus.feature.main.community.components.money
import java.time.Instant

@Composable
fun SettlementRoute(
    afterpartyId: Long,
    settlementId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    val vm: SettlementViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val reload = { vm.load(afterpartyId, settlementId) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { reload() }
    SettlementScreen(state, onBack, onEdit, vm::selectFilter, vm::updatePayment, reload)
}

@Composable
internal fun SettlementScreen(
    state: SettlementScreenState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onFilter: (SettlementPaymentStatus) -> Unit,
    onPayment: (SettlementPaymentStatus) -> Unit,
    onReload: () -> Unit,
) {
    val isCreator =
        state.detail
            .valueOrNull()
            ?.item
            ?.isCreator == true
    val screenTitle = if (isCreator) R.string.community_settlement_state else R.string.community_my_settlements
    Column(Modifier.fillMaxSize()) {
        CommunityHeader(stringResource(screenTitle), onBack)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (val detail = state.detail) {
                null -> CommunityLoading()
                is YappResult.Failure -> CommunityError(detail.reason)
                is YappResult.Success -> {
                    state.afterparty?.let { AfterpartyInformation(it) }
                    CommunityTitle(stringResource(R.string.community_settlement_title, detail.value.item.creatorName))
                    if (isCreator) {
                        SettlementOwner(state, onEdit, onFilter)
                    } else {
                        SettlementPayment(detail.value, state.submitting, onPayment)
                    }
                    SettlementReceipts(detail.value.photos, onReload)
                    state.failure?.let { CommunityError(it, isRequest = true) }
                }
            }
        }
    }
}

@Composable
internal fun SettlementOwner(
    state: SettlementScreenState,
    onEdit: () -> Unit,
    onFilter: (SettlementPaymentStatus) -> Unit,
) {
    when (val summary = state.summary) {
        null -> CommunityLoading()
        is YappResult.Failure -> CommunityError(summary.reason)
        is YappResult.Success -> SettlementTotals(summary.value)
    }
    YappButton(
        stringResource(R.string.community_edit),
        onClick = onEdit,
        variant = YappButtonVariant.OutlinedAssistive,
    )
    CommunityFilters(
        options = listOf(SettlementPaymentStatus.UNPAID, SettlementPaymentStatus.PAID),
        selected = state.filter,
        label = { stringResource(it.label()) },
        onSelected = onFilter,
    )
    CommunityCaption(stringResource(R.string.community_reminder))
    when (val people = state.people) {
        null -> CommunityLoading()
        is YappResult.Failure -> CommunityError(people.reason)
        is YappResult.Success -> {
            if (people.value.isEmpty()) CommunityCaption(stringResource(R.string.community_payment_empty))
            val amount = state.summary.valueOrNull()?.perPersonAmount
            people.value.forEach { person ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CommunityPerson(person, Modifier.weight(1f))
                    amount?.let { CommunityCaption(money(it)) }
                }
            }
        }
    }
}

@Composable
internal fun SettlementTotals(summary: SettlementSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SettlementTile(
                stringResource(R.string.community_receivable),
                money(summary.expectedReceivableAmount),
                Modifier.weight(1f),
            )
            SettlementTile(
                stringResource(R.string.community_paid),
                stringResource(R.string.community_paid_count, summary.paidCount, summary.debtorCount),
                Modifier.weight(1f),
            )
        }
        val progress = if (summary.debtorCount == 0) 0f else summary.paidCount.toFloat() / summary.debtorCount
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = YappMainColor.brand,
            trackColor = YappMainColor.layerFill,
        )
        CommunityCaption(stringResource(R.string.community_remaining, money(summary.remainingAmount)))
    }
}

@Composable
private fun SettlementTile(
    label: String,
    value: String,
    modifier: Modifier,
) {
    Surface(modifier, color = YappMainColor.layerFill, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CommunityCaption(label)
            CommunityTitle(value)
        }
    }
}

@Composable
internal fun SettlementPayment(
    detail: SettlementDetail,
    submitting: Boolean,
    onPayment: (SettlementPaymentStatus) -> Unit,
) {
    val payment = detail.item.paymentStatus ?: return
    val clipboard = LocalContext.current.getSystemService(ClipboardManager::class.java)
    val account = detail.account
    val copyLabel = stringResource(R.string.community_account)
    var copied by rememberSaveable { mutableStateOf(false) }
    CommunityTitle(money(detail.item.myAmount))
    CommunityCaption(stringResource(payment.label()))
    CommunityCaption(stringResource(R.string.community_pay_guide))
    Surface(color = YappMainColor.layerFill, shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                CommunityTitle("${bankLabel(account.bank)} ${account.number}")
                CommunityCaption(stringResource(R.string.community_account_holder, account.holder))
            }
            YappButton(
                text = stringResource(R.string.community_copy),
                onClick = {
                    val clip = ClipData.newPlainText(copyLabel, account.number)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        clip.description.extras =
                            PersistableBundle().apply {
                                putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                            }
                    }
                    clipboard?.setPrimaryClip(clip)
                    copied = clipboard != null
                },
                variant = YappButtonVariant.OutlinedAssistive,
            )
        }
    }
    if (copied) CommunityCaption(stringResource(R.string.community_copied))
    val isPaid = payment == SettlementPaymentStatus.PAID
    YappButton(
        text = stringResource(if (isPaid) R.string.community_undo_payment else R.string.community_paid_action),
        onClick = { onPayment(if (isPaid) SettlementPaymentStatus.UNPAID else SettlementPaymentStatus.PAID) },
        enabled = !submitting,
        modifier = Modifier.fillMaxWidth(),
        variant = if (isPaid) YappButtonVariant.OutlinedAssistive else YappButtonVariant.SolidBrand,
    )
}

@Composable
internal fun SettlementReceipts(
    photos: List<SettlementPhoto>,
    onReload: () -> Unit,
) {
    if (photos.isEmpty()) return
    var selected by rememberSaveable { mutableStateOf<Long?>(null) }
    CommunityTitle(stringResource(R.string.community_proofs))
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        photos.forEach { photo ->
            if (photo.url != null) {
                ProtectedPhoto(
                    data = photo.activeUrl(Instant.now()),
                    modifier =
                        Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).noRippleClickable {
                            selected = photo.id
                            if (photo.activeUrl(Instant.now()) == null) onReload()
                        },
                )
            }
        }
    }
    if (selected != null) {
        val image = photos.firstOrNull { it.id == selected }?.activeUrl(Instant.now())
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(stringResource(R.string.community_proofs)) },
            text = {
                if (image == null) {
                    CommunityError()
                } else {
                    ProtectedPhoto(
                        image,
                        Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 420.dp),
                        ContentScale.Fit,
                    )
                }
            },
            confirmButton = {
                WithoutRipple {
                    TextButton(onClick = { selected = null }) { Text(stringResource(R.string.community_confirm)) }
                }
            },
        )
    }
}
