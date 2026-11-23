package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.session.SessionCoordinates

internal class HomeAttendanceAttempt(
    val sessionId: Long,
    val code: Int,
    val coordinates: SessionCoordinates,
) {
    fun matches(session: CurrentWeekSessionUiModel?): Boolean =
        session?.sessionId == sessionId &&
            session.coordinates == coordinates &&
            session.attendanceStatus == AttendanceStatusUiModel.Available
}
