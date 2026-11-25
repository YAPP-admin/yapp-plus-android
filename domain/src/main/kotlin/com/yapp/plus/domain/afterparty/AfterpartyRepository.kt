package com.yapp.plus.domain.afterparty

interface AfterpartyRepository {
    suspend fun fetchAfterparties(
        filter: AfterpartyFilter,
        cursor: String? = null,
    ): AfterpartyResult<AfterpartyPage<AfterpartySummary>>

    suspend fun fetchAfterparty(id: Long): AfterpartyResult<AfterpartyDetail>

    suspend fun fetchAttendees(
        id: Long,
        cursor: String? = null,
    ): AfterpartyResult<AfterpartyPage<AfterpartyPerson>>

    suspend fun updateResponse(
        id: Long,
        response: AfterpartyResponse,
    ): AfterpartyResult<AfterpartyResponse>
}
