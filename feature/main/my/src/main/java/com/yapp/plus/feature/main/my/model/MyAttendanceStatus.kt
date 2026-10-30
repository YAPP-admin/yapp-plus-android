package com.yapp.plus.feature.main.my.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.main.my.R

enum class MyAttendanceStatus(
    @param:StringRes val label: Int,
) {
    Present(R.string.my_status_present),
    Late(R.string.my_status_late),
    Absent(R.string.my_status_absent),
    EarlyLeave(R.string.my_status_early_leave),
    LateAndEarlyLeave(R.string.my_status_late_early_leave),
    ExcusedAbsence(R.string.my_status_excused),
    Undecided(R.string.my_status_undecided),
    Pending(R.string.my_status_pending),
    Rejected(R.string.my_status_rejected),
    NotSubmitted(R.string.my_status_not_submitted),
    NotSelected(R.string.my_status_not_selected),
}
