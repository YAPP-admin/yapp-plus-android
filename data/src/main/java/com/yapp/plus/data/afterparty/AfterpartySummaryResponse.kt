package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartySchedule
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.AfterpartySummary
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
internal data class AfterpartySummaryResponse(
    val afterpartyId: Long,
    val title: String,
    val startsAt: String,
    val startsAtDisplay: String,
    val deadline: String,
    val deadlineDisplay: String,
    val respondedCount: Int,
    val state: AfterpartyState,
) {
    fun toDomain(): AfterpartySummary =
        AfterpartySummary(
            afterpartyId,
            title,
            AfterpartySchedule(Instant.parse(startsAt), startsAtDisplay, Instant.parse(deadline), deadlineDisplay),
            state,
            respondedCount,
        )
}
