package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementSummary

internal data class SettlementScreenState(
    val detail: AfterpartyResult<SettlementDetail>? = null,
    val afterparty: AfterpartyDetail? = null,
    val summary: AfterpartyResult<SettlementSummary>? = null,
    val people: AfterpartyResult<List<AfterpartyPerson>>? = null,
    val filter: SettlementPaymentStatus = SettlementPaymentStatus.UNPAID,
    val failure: AfterpartyFailure? = null,
    val submitting: Boolean = false,
)
