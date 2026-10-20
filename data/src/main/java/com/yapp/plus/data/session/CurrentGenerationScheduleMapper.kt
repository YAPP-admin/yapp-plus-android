package com.yapp.plus.data.session

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal object CurrentGenerationScheduleMapper {
    fun toData(response: CurrentGenerationScheduleResponse): CurrentGenerationScheduleData =
        CurrentGenerationScheduleData(
            generationId = response.generationId,
            generationNumber = response.generationNumber,
            sessions = response.sessions.map(::toData),
        )

    fun toDomain(data: CurrentGenerationScheduleData): CurrentGenerationSchedule =
        CurrentGenerationSchedule(
            generationId = data.generationId,
            generationNumber = data.generationNumber,
            sessions = data.sessions.map(::toDomain),
        )

    private fun toData(response: ScheduledSessionResponse): ScheduledSessionData =
        ScheduledSessionData(
            sessionId = response.sessionId,
            name = response.name,
            date = LocalDate.parse(response.date),
            dayOfWeek = DayOfWeek.valueOf(response.dayOfWeek),
            location = response.location,
            startTime = LocalTime.parse(response.startTime),
            endTime = LocalTime.parse(response.endTime),
            type = SessionType.valueOf(response.type),
        )

    private fun toDomain(data: ScheduledSessionData): ScheduledSession =
        ScheduledSession(
            sessionId = data.sessionId,
            name = data.name,
            date = data.date,
            dayOfWeek = data.dayOfWeek,
            location = data.location,
            startTime = data.startTime,
            endTime = data.endTime,
            type = data.type,
        )
}
