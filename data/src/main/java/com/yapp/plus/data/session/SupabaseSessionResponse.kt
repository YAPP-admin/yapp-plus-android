package com.yapp.plus.data.session

import com.yapp.plus.domain.session.SessionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
internal data class SupabaseSessionResponse(
    val id: Long,
    val name: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    val location: String? = null,
    @SerialName("session_type") val sessionType: String,
    @SerialName("attendance_status") val attendanceStatus: String,
) {
    fun toData(zone: ZoneId): ScheduledSessionData {
        val start = OffsetDateTime.parse(startsAt).atZoneSameInstant(zone)
        val end = OffsetDateTime.parse(endsAt).atZoneSameInstant(zone)
        require(id > 0 && name.isNotBlank() && end.isAfter(start))
        return ScheduledSessionData(
            sessionId = id,
            name = name,
            date = start.toLocalDate(),
            dayOfWeek = start.dayOfWeek,
            location = location,
            startTime = start.toLocalTime(),
            endTime = end.toLocalTime(),
            type = SessionType.valueOf(sessionType),
        )
    }
}
