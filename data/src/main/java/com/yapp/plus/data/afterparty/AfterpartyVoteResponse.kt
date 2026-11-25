package com.yapp.plus.data.afterparty

import com.yapp.plus.domain.afterparty.AfterpartyResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class AfterpartyVoteResponse(
    val response: AfterpartyResponse,
)
