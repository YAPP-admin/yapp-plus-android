package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyPerson
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementSummary

internal data class AfterpartyScreenState(
    val detail: AfterpartyResult<AfterpartyDetail>? = null,
    val attendees: AfterpartyResult<List<AfterpartyPerson>>? = null,
    val settlements: AfterpartyResult<List<SettlementItem>>? = null,
    val ownerSummaries: Map<Long, SettlementSummary> = emptyMap(),
    val submitting: Boolean = false,
    val failure: AfterpartyFailure? = null,
)
