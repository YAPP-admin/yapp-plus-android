package com.yapp.plus.data.session

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import java.time.LocalDate
import java.time.LocalTime

internal fun CurrentWeekScheduleResponse.toDomain(): CurrentWeekSchedule =
    CurrentWeekSchedule(
        weekStartDate = LocalDate.parse(weekStartDate),
        weekEndDate = LocalDate.parse(weekEndDate),
        session = session?.toDomain(),
    )

private fun CurrentWeekSessionResponse.toDomain(): CurrentWeekSession =
    CurrentWeekSession(
        sessionId = sessionId,
        name = name,
        date = LocalDate.parse(date),
        startTime = LocalTime.parse(startTime),
        location = location,
        attendanceStatus = AttendanceStatus.valueOf(attendanceStatus),
    )
