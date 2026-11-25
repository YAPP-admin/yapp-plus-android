package com.yapp.plus.core.ui.model

import androidx.annotation.StringRes
import com.yapp.plus.core.ui.R

enum class AttendanceStatusUiModel(
    @param:StringRes val messageResource: Int,
) {
    NotEligible(R.string.attendance_status_not_eligible),
    NotOpen(R.string.attendance_status_not_open),
    Available(R.string.attendance_status_available),
    CheckedIn(R.string.home_attended),
    PendingApproval(R.string.home_team_result_pending_action),
}
