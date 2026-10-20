package com.yapp.plus.data.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
internal data class SupabaseContextResponse(
    @SerialName("generation_id") val generationId: Long?,
    @SerialName("generation_number") val generationNumber: Int?,
    @SerialName("time_zone") val timeZone: String,
    @SerialName("week_start_date") val weekStartDate: String,
    @SerialName("week_end_date") val weekEndDate: String,
) {
    val weekStart: LocalDate get() = LocalDate.parse(weekStartDate)
    val weekEnd: LocalDate get() = LocalDate.parse(weekEndDate)
}
