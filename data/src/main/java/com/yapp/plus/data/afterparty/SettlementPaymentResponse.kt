package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class SettlementPaymentResponse(
    val paymentStatus: SettlementPaymentStatus,
)
