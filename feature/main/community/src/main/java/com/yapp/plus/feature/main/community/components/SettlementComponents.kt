package com.yapp.plus.feature.main.community.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.yapp.plus.core.designsystem.components.surface.YappClickableSurface
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.afterparty.SettlementBank
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.feature.main.community.R
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun SettlementRow(
    item: SettlementItem,
    ownerAmount: Long? = null,
    onClick: () -> Unit,
) {
    YappClickableSurface(onClick = onClick, color = YappMainColor.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    YappText(
                        stringResource(R.string.community_settlement_title, item.creatorName),
                        YappTypography.scaleT5Bold,
                        YappMainColor.textPrimary,
                        Modifier.weight(1f, fill = false),
                    )
                    if (item.isCreator) SettlementBadge(stringResource(R.string.community_myself))
                }
                YappText(
                    stringResource(R.string.community_settlement_count, item.participantCount),
                    YappTypography.scaleT4Regular,
                    YappMainColor.textSubtle,
                )
            }
            val amount = if (item.isCreator) ownerAmount else item.myAmount
            YappText(
                amount?.let { money(it) } ?: stringResource(R.string.community_unknown_value),
                YappTypography.scaleT5Bold,
                YappMainColor.textPrimary,
            )
            Image(
                painterResource(R.drawable.settlement_chevron_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
internal fun money(amount: Long): String = money(BigInteger.valueOf(amount))

@Composable
internal fun money(amount: BigInteger): String =
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
