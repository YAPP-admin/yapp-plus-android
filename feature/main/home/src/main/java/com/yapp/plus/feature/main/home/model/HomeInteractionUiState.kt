package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.attendance.AttendanceReportType

data class HomeInteractionUiState(
    val sheet: HomeSheet = HomeSheet.None,
    val code: String = "",
    val reason: String = "",
    val reportChoice: ReportChoice? = null,
    val reportType: AttendanceReportType? = null,
    val isSubmitting: Boolean = false,
    val errorResource: Int? = null,
    val isInvalidCode: Boolean = false,
    val attendedSessionId: Long? = null,
    val isAttendanceComplete: Boolean = false,
    val isReportComplete: Boolean = false,
    val reportedSessionId: Long? = null,
)
