package com.yapp.plus.data.session

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal object CurrentGenerationScheduleMapper {
    fun toDomain(response: CurrentGenerationScheduleResponse): CurrentGenerationSchedule =
        CurrentGenerationSchedule(
            generationId = response.generationId,
            generationNumber = response.generationNumber,
            sessions = response.sessions.map(::toDomain),
        )

    private fun toDomain(response: ScheduledSessionResponse): ScheduledSession =
        ScheduledSession(
            sessionId = response.sessionId,
            name = response.name,
            date = LocalDate.parse(response.date),
            dayOfWeek = DayOfWeek.valueOf(response.dayOfWeek),
            location = response.location,
            startTime = LocalTime.parse(response.startTime),
            endTime = LocalTime.parse(response.endTime),
            type = SessionType.valueOf(response.type),
        )
}
