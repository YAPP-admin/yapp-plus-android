package com.yapp.plus.feature.attendance.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.attendance.R

enum class BoardCategory(
    @param:StringRes val labelResource: Int,
) {
    All(R.string.attendance_board_all),
    Session(R.string.attendance_board_session),
    Assignment(R.string.attendance_board_assignment),
    Other(R.string.attendance_board_other),
}
