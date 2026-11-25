package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import io.ktor.client.HttpClient

class AfterpartyRepositoryImpl internal constructor(
    private val source: AfterpartyDataSource,
) : AfterpartyRepository {
    override suspend fun fetchAfterparties(
        filter: AfterpartyFilter,
        cursor: String?,
    ) = afterpartyRequest { source.fetchList(filter, cursor).toDomain(AfterpartySummaryResponse::toDomain) }

    override suspend fun fetchAfterparty(id: Long) = afterpartyRequest { source.fetchDetail(id).toDomain() }

    override suspend fun fetchAttendees(
        id: Long,
        cursor: String?,
    ) = afterpartyRequest { source.fetchAttendees(id, cursor).toDomain(AfterpartyPersonResponse::toDomain) }

    override suspend fun updateResponse(
        id: Long,
        response: AfterpartyResponse,
    ) = afterpartyRequest { source.updateResponse(id, response).response }

    companion object {
        fun create(client: HttpClient): AfterpartyRepository =
            AfterpartyRepositoryImpl(AfterpartyRemoteDataSource(client))
    }
}
