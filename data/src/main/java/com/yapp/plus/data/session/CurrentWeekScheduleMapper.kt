package com.yapp.plus.data.session

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import java.time.LocalDate
import java.time.LocalTime

internal object CurrentWeekScheduleMapper {
    fun toData(response: CurrentWeekScheduleResponse): CurrentWeekScheduleData =
        CurrentWeekScheduleData(
            weekStartDate = LocalDate.parse(response.weekStartDate),
            weekEndDate = LocalDate.parse(response.weekEndDate),
            session = response.session?.let(::toData),
        )

    fun toDomain(data: CurrentWeekScheduleData): CurrentWeekSchedule =
        CurrentWeekSchedule(
            weekStartDate = data.weekStartDate,
            weekEndDate = data.weekEndDate,
            session = data.session?.let(::toDomain),
        )

    private fun toData(response: CurrentWeekSessionResponse): CurrentWeekSessionData =
        CurrentWeekSessionData(
            sessionId = response.sessionId,
            name = response.name,
            date = LocalDate.parse(response.date),
            startTime = LocalTime.parse(response.startTime),
            location = response.location,
            attendanceStatus = AttendanceStatus.valueOf(response.attendanceStatus),
        )

    private fun toDomain(data: CurrentWeekSessionData): CurrentWeekSession =
        CurrentWeekSession(
            sessionId = data.sessionId,
            name = data.name,
            date = data.date,
            startTime = data.startTime,
            location = data.location,
            attendanceStatus = data.attendanceStatus,
        )
}
