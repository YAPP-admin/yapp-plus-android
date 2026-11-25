package com.yapp.plus.feature.main.home.attendance.model

import com.yapp.plus.domain.session.SessionCoordinates

class HomeAttendanceAttempt(
    val sessionId: Long,
    val code: Int,
    val coordinates: SessionCoordinates,
) {
    fun matches(
        sessionPageSessionId: Long?,
        sessionPageCoordinates: SessionCoordinates?,
        isSessionAvailable: Boolean,
    ): Boolean =
        sessionPageSessionId == sessionId &&
            sessionPageCoordinates == coordinates &&
            isSessionAvailable
}
