package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.attendance.AttendanceReportType

data class HomeInteractionUiState(
    val sheet: HomeSheet = HomeSheet.None,
    val code: String = "",
    val submissionStage: HomeSubmissionStage = HomeSubmissionStage.Idle,
    val locationIssue: HomeAttendanceLocationIssue? = null,
    val errorResource: Int? = null,
    val isInvalidCode: Boolean = false,
    val verifiedSessionId: Long? = null,
    val reportType: AttendanceReportType? = null,
    val reportReason: String = "",
    val isReportComplete: Boolean = false,
) {
    val isSubmitting: Boolean
        get() = submissionStage != HomeSubmissionStage.Idle
}
