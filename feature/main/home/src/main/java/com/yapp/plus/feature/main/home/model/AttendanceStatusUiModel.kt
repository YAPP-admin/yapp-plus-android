package com.yapp.plus.feature.main.home.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.main.home.R

enum class AttendanceStatusUiModel(
    @param:StringRes val messageResource: Int,
) {
    NotEligible(R.string.attendance_status_not_eligible),
    NotOpen(R.string.attendance_status_not_open),
    Available(R.string.attendance_status_available),
}
