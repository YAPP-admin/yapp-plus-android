package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.attendance.AttendanceReportType

data class HomeInteractionUiState(
    val sheet: HomeSheet = HomeSheet.None,
    val code: String = "",
    val isSubmitting: Boolean = false,
    val errorResource: Int? = null,
    val isInvalidCode: Boolean = false,
    val attendedSessionId: Long? = null,
    val isAttendanceComplete: Boolean = false,
    val reportType: AttendanceReportType? = null,
    val reportReason: String = "",
    val isReportComplete: Boolean = false,
)
