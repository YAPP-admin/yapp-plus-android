package com.yapp.plus.data.session

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal fun CurrentGenerationScheduleResponse.toDomain(): CurrentGenerationSchedule =
    CurrentGenerationSchedule(
        generationId = generationId,
        generationNumber = generationNumber,
        sessions = sessions.map(ScheduledSessionResponse::toDomain),
    )

private fun ScheduledSessionResponse.toDomain(): ScheduledSession =
    ScheduledSession(
        sessionId = sessionId,
        name = name,
        date = LocalDate.parse(date),
        dayOfWeek = DayOfWeek.valueOf(dayOfWeek),
        location = location,
        startTime = LocalTime.parse(startTime),
        endTime = LocalTime.parse(endTime),
        type = SessionType.valueOf(type),
    )
