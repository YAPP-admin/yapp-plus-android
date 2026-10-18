package com.yapp.plus.data.session

import kotlinx.serialization.Serializable

@Serializable
internal data class CurrentWeekScheduleResponse(
    val weekStartDate: String,
    val weekEndDate: String,
    val session: CurrentWeekSessionResponse? = null,
)
