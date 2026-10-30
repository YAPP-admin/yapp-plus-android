package com.yapp.plus.feature.main.my.model

import com.yapp.plus.domain.attendance.AttendanceScore

sealed interface MyAttendanceScoreState {
    data object Loading : MyAttendanceScoreState

    data object Error : MyAttendanceScoreState

    data object Ineligible : MyAttendanceScoreState

    data class Content(
        val score: AttendanceScore,
        val lateExemptionCount: Int? = null,
    ) : MyAttendanceScoreState
}
