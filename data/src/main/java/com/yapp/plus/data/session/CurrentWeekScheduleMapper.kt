package com.yapp.plus.data.session

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import java.time.LocalDate
import java.time.LocalTime

internal object CurrentWeekScheduleMapper {
    fun toDomain(response: CurrentWeekScheduleResponse): CurrentWeekSchedule =
        CurrentWeekSchedule(
            weekStartDate = LocalDate.parse(response.weekStartDate),
            weekEndDate = LocalDate.parse(response.weekEndDate),
            session = response.session?.let(::toDomain),
        )

    private fun toDomain(response: CurrentWeekSessionResponse): CurrentWeekSession =
        CurrentWeekSession(
            sessionId = response.sessionId,
            name = response.name,
            date = LocalDate.parse(response.date),
            startTime = LocalTime.parse(response.startTime),
            location = response.location,
            attendanceStatus = AttendanceStatus.valueOf(response.attendanceStatus),
        )
}
