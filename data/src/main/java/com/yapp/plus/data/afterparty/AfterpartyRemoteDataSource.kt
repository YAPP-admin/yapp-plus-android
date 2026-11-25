package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import io.ktor.client.HttpClient
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class AfterpartyRemoteDataSource(
    private val client: HttpClient,
) : AfterpartyDataSource {
    override suspend fun fetchList(
        filter: AfterpartyFilter,
        cursor: String?,
    ): CursorResponse<AfterpartySummaryResponse> =
        client.afterpartyGet("afterparties") {
            append("filter", filter.name)
            cursor?.let { append("cursor", it) }
        }

    override suspend fun fetchDetail(id: Long): AfterpartyDetailResponse = client.afterpartyGet("afterparties/$id")

    override suspend fun fetchAttendees(
        id: Long,
        cursor: String?,
    ): CursorResponse<AfterpartyPersonResponse> =
        client.afterpartyGet("afterparties/$id/attendees") {
            cursor?.let { append("cursor", it) }
        }

    override suspend fun updateResponse(
        id: Long,
        response: AfterpartyResponse,
    ): AfterpartyVoteResponse =
        client.afterpartyPut("afterparties/$id/response", buildJsonObject { put("response", response.name) })
}
