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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yapp.plus.core.designsystem.modifier.WithoutRipple
import com.yapp.plus.core.designsystem.modifier.noRippleClickable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementPhoto
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.components.CommunityError
import com.yapp.plus.feature.main.community.components.CommunityHeader
import com.yapp.plus.feature.main.community.components.CommunityLoading
import com.yapp.plus.feature.main.community.components.CommunityTitle
import com.yapp.plus.feature.main.community.components.ProtectedPhoto
import com.yapp.plus.feature.main.community.components.SettlementOwner
import com.yapp.plus.feature.main.community.components.SettlementPaymentContent
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
    SettlementScreen(
        state,
        onBack,
        onEdit,
        vm::selectFilter,
        vm::updatePayment,
        reload,
        vm::dismissPaymentNotice,
    )
}

@Composable
internal fun SettlementScreen(
    state: SettlementScreenState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onFilter: (SettlementPaymentStatus) -> Unit,
    onPayment: (Long, SettlementPaymentStatus) -> Unit,
    onReload: () -> Unit,
    onDismissPaymentNotice: () -> Unit = {},
) {
    val isCreator =
        state.detail
            .valueOrNull()
            ?.item
            ?.isCreator == true
    val title = if (isCreator) R.string.community_settlement_state else R.string.community_my_settlements
    Column(Modifier.fillMaxSize().background(YappMainColor.surface)) {
        CommunityHeader(stringResource(title), onBack)
        when (val detail = state.detail) {
            null -> CommunityLoading()
            is YappResult.Failure -> Box(Modifier.padding(20.dp)) { CommunityError(detail.reason) }
            is YappResult.Success ->
                if (isCreator) {
                    SettlementOwner(state, onEdit, onFilter, onReload)
                } else {
                    SettlementPaymentContent(state, detail.value, onPayment, onReload, onDismissPaymentNotice)
                }
        }
    }
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
