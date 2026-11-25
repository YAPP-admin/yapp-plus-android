package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyResponse

internal interface AfterpartyDataSource {
    suspend fun fetchList(
        filter: AfterpartyFilter,
        cursor: String?,
    ): CursorResponse<AfterpartySummaryResponse>

    suspend fun fetchDetail(id: Long): AfterpartyDetailResponse

    suspend fun fetchAttendees(
        id: Long,
        cursor: String?,
    ): CursorResponse<AfterpartyPersonResponse>

    suspend fun updateResponse(
        id: Long,
        response: AfterpartyResponse,
    ): AfterpartyVoteResponse
}
