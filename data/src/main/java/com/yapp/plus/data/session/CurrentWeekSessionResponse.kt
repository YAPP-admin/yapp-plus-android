package com.yapp.plus.data.session

import kotlinx.serialization.Serializable

@Serializable
internal data class CurrentWeekSessionResponse(
    val sessionId: Long,
    val name: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val type: String,
    val location: String,
    val attendanceStatus: String,
    val roadAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val detailAddress: String? = null,
)
