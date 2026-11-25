package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.feature.main.community.R
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun SettlementRow(
    item: SettlementItem,
    onClick: () -> Unit,
) {
    YappClickableSurface(onClick = onClick, color = YappMainColor.surface) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CommunityTitle(stringResource(R.string.community_settlement_title, item.creatorName))
                CommunityCaption(stringResource(R.string.community_settlement_count, item.participantCount))
            }
            if (item.isCreator) {
                CommunityCaption(stringResource(R.string.community_myself))
            } else {
                CommunityCaption(money(item.myAmount))
            }
        }
    }
    HorizontalDivider(color = YappMainColor.divider)
}

@Composable
internal fun money(amount: Long): String =
    stringResource(R.string.community_money, NumberFormat.getIntegerInstance(Locale.KOREA).format(amount))

@Composable
internal fun bankLabel(bank: SettlementBank): String = stringArrayResource(R.array.community_banks)[bank.ordinal]

internal fun SettlementPaymentStatus.label(): Int =
    if (this == SettlementPaymentStatus.PAID) R.string.community_paid else R.string.community_unpaid

@Composable
internal fun ProtectedPhoto(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val request =
        remember(context, data) {
            ImageRequest
                .Builder(context)
                .data(data)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()
        }
    AsyncImage(
        model = request,
        contentDescription = stringResource(R.string.community_proofs),
        modifier = modifier,
        contentScale = contentScale,
    )
}
