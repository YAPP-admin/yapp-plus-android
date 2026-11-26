package com.yapp.plus.data.session

import com.yapp.plus.domain.session.AttendanceStatus
import com.yapp.plus.domain.session.CurrentWeekSchedule
import com.yapp.plus.domain.session.CurrentWeekSession
import com.yapp.plus.domain.session.SessionType
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

    private fun toData(response: CurrentWeekSessionResponse): CurrentWeekSessionData {
        val type = SessionType.valueOf(response.type)
        val location =
            when (type) {
                SessionType.REGULAR -> requireNotNull(response.location)
                SessionType.TEAM -> response.location.orEmpty()
            }

        return CurrentWeekSessionData(
            sessionId = response.sessionId,
            name = response.name,
            date = LocalDate.parse(response.date),
            startTime = LocalTime.parse(response.startTime),
            endTime = LocalTime.parse(response.endTime),
            type = type,
            location = location,
            attendanceStatus = AttendanceStatus.valueOf(response.attendanceStatus),
            locationDetails =
                SessionLocationData(
                    roadAddress = response.roadAddress,
                    latitude = response.latitude,
                    longitude = response.longitude,
                    detailAddress = response.detailAddress,
                ).takeIfPresent(),
        )
    }

    private fun toDomain(data: CurrentWeekSessionData): CurrentWeekSession =
        CurrentWeekSession(
            sessionId = data.sessionId,
            name = data.name,
            date = data.date,
            startTime = data.startTime,
            endTime = data.endTime,
            type = data.type,
            location = data.location,
            attendanceStatus = data.attendanceStatus,
            locationDetails = data.locationDetails?.toDomain(),
        )
}
