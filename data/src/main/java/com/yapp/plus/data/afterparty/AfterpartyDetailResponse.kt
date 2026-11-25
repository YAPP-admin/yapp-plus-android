package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyDetail
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.afterparty.AfterpartySchedule
import com.yapp.plus.domain.afterparty.AfterpartyState
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
internal data class AfterpartyDetailResponse(
    val afterpartyId: Long,
    val title: String,
    val content: String,
    val startsAt: String,
    val startsAtDisplay: String,
    val deadline: String,
    val deadlineDisplay: String,
    val state: AfterpartyState,
    val response: AfterpartyResponse,
    val attendingCount: Int?,
) {
    fun toDomain(): AfterpartyDetail =
        AfterpartyDetail(
            afterpartyId,
            title,
            content,
            AfterpartySchedule(Instant.parse(startsAt), startsAtDisplay, Instant.parse(deadline), deadlineDisplay),
            state,
            response,
            attendingCount,
        )
}
