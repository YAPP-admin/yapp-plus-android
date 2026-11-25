package com.yapp.plus.feature.main.community.components

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Build
import android.os.PersistableBundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementPhoto
import com.yapp.plus.feature.main.community.R
import com.yapp.plus.feature.main.community.SettlementScreenState
import com.yapp.plus.feature.main.community.unpaidAmount
import com.yapp.plus.feature.main.community.valueOrNull
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
internal fun SettlementPaymentContent(
    state: SettlementScreenState,
    anchor: SettlementDetail,
    onPayment: (Long, SettlementPaymentStatus) -> Unit,
    onReload: () -> Unit,
    onDismissPaymentNotice: () -> Unit,
) {
    if (state.payments == null) {
        CommunityLoading()
        return
    }
    val loaded = state.payments.valueOrNull()
    val entries = loaded ?: listOf(anchor)
    var showCopied by rememberSaveable { mutableStateOf(false) }
    var undoId by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(state.paymentCompleted) {
        if (state.paymentCompleted) {
            showCopied = false
            delay(3_000)
            onDismissPaymentNotice()
        }
    }
    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize()) {
            item { SettlementPaymentSummary(loaded) }
            if (loaded == null || state.failure != null) {
                item {
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        CommunityError(state.failure, isRequest = state.failure != null)
                    }
                }
            }
            items(entries, key = { it.item.id }) { detail ->
                SettlementSectionDivider()
                SettlementPayment(
                    detail = detail,
                    submitting = state.submittingSettlementId != null,
                    onCopy = {
                        onDismissPaymentNotice()
                        showCopied = true
                    },
                    onUndo = { undoId = detail.item.id },
                    onReload = onReload,
                    onPayment = { onPayment(detail.item.id, it) },
                )
            }
        }
        if (showCopied) {
            SettlementNotice(
                stringResource(R.string.community_copied),
                onDismiss = { showCopied = false },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        } else if (state.paymentCompleted) {
            SettlementNotice(
                stringResource(R.string.community_marked_paid),
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
    undoId?.let { id ->
        SettlementUndoDialog(
            onDismiss = { undoId = null },
            onConfirm = {
                undoId = null
                onPayment(id, SettlementPaymentStatus.UNPAID)
            },
        )
    }
}

@Composable
internal fun SettlementPaymentSummary(details: List<SettlementDetail>?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            YappText(
                stringResource(R.string.community_remaining_total),
                YappTypography.scaleT5Regular,
                YappMainColor.textSecondary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val value = details?.map { it.item }?.unpaidAmount()?.let { money(it) }
                YappText(
                    value ?: stringResource(R.string.community_unknown_value),
                    YappTypography.scaleT8Bold,
                    YappMainColor.textPrimary,
                    Modifier.weight(1f),
                )
                if (details != null) {
                    val paid = details.count { it.item.paymentStatus == SettlementPaymentStatus.PAID }
                    YappText(
                        stringResource(R.string.community_payment_progress, paid, details.size),
                        YappTypography.scaleT4Regular,
                        YappMainColor.textSubtle,
                    )
                }
            }
        }
        SettlementCallout(stringResource(R.string.community_pay_guide))
    }
}

@Composable
internal fun SettlementPayment(
    detail: SettlementDetail,
    submitting: Boolean,
    onCopy: () -> Unit = {},
    onUndo: () -> Unit = {},
    onReload: () -> Unit = {},
    onPayment: (SettlementPaymentStatus) -> Unit,
) {
    val payment = detail.item.paymentStatus ?: return
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SettlementPaymentHeading(detail, payment, onReload)
        if (payment == SettlementPaymentStatus.PAID) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                SettlementControl(
                    text = stringResource(R.string.community_undo_payment),
                    outlined = true,
                    enabled = !submitting,
                    onClick = onUndo,
                )
            }
        } else {
            SettlementPaymentAccount(detail, onCopy)
            SettlementControl(
                text = stringResource(R.string.community_paid_action),
                background = YappMainColor.brand,
                foreground = YappMainColor.surface,
                height = 40.dp,
                enabled = !submitting,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onPayment(SettlementPaymentStatus.PAID) },
            )
        }
    }
}

@Composable
private fun SettlementPaymentHeading(
    detail: SettlementDetail,
    payment: SettlementPaymentStatus,
    onReload: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YappText(
                    stringResource(R.string.community_settlement_title, detail.item.creatorName),
                    YappTypography.scaleT5Bold,
                    YappMainColor.textPrimary,
                    Modifier.weight(1f, fill = false),
                )
                SettlementPaymentBadge(payment)
            }
            YappText(money(detail.item.myAmount), YappTypography.scaleT7Bold, YappMainColor.textPrimary)
        }
        if (detail.photos.isNotEmpty()) SettlementPaymentProof(detail.photos, onReload)
    }
}

@Composable
internal fun SettlementPaymentAccount(
    detail: SettlementDetail,
    onCopy: () -> Unit,
) {
    val account = detail.account
    val copyLabel = stringResource(R.string.community_account)
    val clipboard = LocalContext.current.getSystemService(ClipboardManager::class.java)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(YappMainColor.basement, RoundedCornerShape(12.dp))
                .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            YappText(
                stringResource(R.string.community_bank_number, editorBankLabel(account.bank), account.number),
                YappTypography.scaleT4Bold,
                YappMainColor.textPrimary,
            )
            YappText(
                stringResource(R.string.community_account_holder, account.holder),
                YappTypography.scaleT2Medium,
                YappMainColor.textSecondary,
            )
        }
        SettlementControl(
            text = stringResource(R.string.community_copy),
            background = YappMainColor.basement,
            outlined = true,
            onClick = {
                if (clipboard != null) {
                    clipboard.setPrimaryClip(sensitiveAccountClip(copyLabel, account.number))
                    onCopy()
                }
            },
        )
    }
}

private fun sensitiveAccountClip(
    label: String,
    accountNumber: String,
): ClipData =
    ClipData.newPlainText(label, accountNumber).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            description.extras =
                PersistableBundle().apply {
                    putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                }
        }
    }

@Composable
internal fun SettlementPaymentProof(
    photos: List<SettlementPhoto>,
    onReload: () -> Unit,
) {
    val first = photos.firstOrNull() ?: return
    var showing by rememberSaveable { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    val title = stringResource(R.string.community_proofs)
    Box(
        Modifier
            .size(48.dp)
            .clip(shape)
            .background(YappMainColor.layerFill)
            .border(1.dp, YappMainColor.divider, shape)
            .noRippleClickable(role = Role.Button, onClickLabel = title) {
                showing = true
                if (photos.any { it.activeUrl(Instant.now()) == null }) onReload()
            },
    ) {
        first.activeUrl(Instant.now())?.let { ProtectedPhoto(it, Modifier.fillMaxSize()) }
        if (photos.size > 1) {
            YappText(
                stringResource(R.string.community_extra_photos, photos.size - 1),
                YappTypography.scaleT1Bold,
                YappMainColor.surface,
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(YappHomeColor.scrim, RoundedCornerShape(50))
                    .padding(horizontal = 5.dp, vertical = 1.dp),
            )
        }
    }
    if (showing) SettlementPaymentPhotos(photos) { showing = false }
}

@Composable
private fun SettlementPaymentPhotos(
    photos: List<SettlementPhoto>,
    onClose: () -> Unit,
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val photo = photos.getOrNull(selected)?.activeUrl(Instant.now())
    AlertDialog(
        onDismissRequest = onClose,
        title = {
            YappText(
                stringResource(R.string.community_proofs),
                YappTypography.scaleT6Bold,
                YappMainColor.textPrimary,
            )
        },
        text = {
            Column {
                if (photo == null) {
                    CommunityError()
                } else {
                    ProtectedPhoto(
                        photo,
                        Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 420.dp),
                        ContentScale.Fit,
                    )
                }
                if (photos.size > 1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        photos.forEachIndexed { index, item ->
                            val url = item.activeUrl(Instant.now())
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(YappMainColor.basement)
                                    .noRippleClickable { selected = index },
                            ) {
                                if (url != null) ProtectedPhoto(url, Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            SettlementControl(stringResource(R.string.community_confirm), onClick = onClose)
        },
    )
}

@Composable
internal fun SettlementUndoDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { window?.setDimAmount(0.46f) }
        Surface(
            modifier = Modifier.widthIn(max = 272.dp).fillMaxWidth(),
            color = YappMainColor.surface,
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    YappText(
                        stringResource(R.string.community_undo_title),
                        YappTypography.scaleT7Bold,
                        YappMainColor.textPrimary,
                    )
                    YappText(
                        stringResource(R.string.community_undo_guide),
                        YappTypography.scaleT4Regular,
                        YappMainColor.textPrimary,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettlementControl(
                        text = stringResource(R.string.community_cancel),
                        modifier = Modifier.weight(1f),
                        background = YappMainColor.basement,
                        height = 40.dp,
                        onClick = onDismiss,
                    )
                    SettlementControl(
                        text = stringResource(R.string.community_undo_payment),
                        modifier = Modifier.weight(1f),
                        background = YappMainColor.neutralSolid,
                        foreground = YappMainColor.surface,
                        height = 40.dp,
                        onClick = onConfirm,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettlementControl(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = YappMainColor.surface,
    foreground: Color = YappMainColor.textPrimary,
    height: Dp = 36.dp,
    outlined: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    YappClickableSurface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) background else YappMainColor.basement,
        border = if (outlined) BorderStroke(1.dp, YappMainColor.border) else null,
        modifier = modifier,
    ) {
        Box(
            Modifier.heightIn(min = height).padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            YappText(
                text,
                YappTypography.scaleT4Bold,
                if (enabled) foreground else YappMainColor.textDisabled,
            )
        }
    }
}
