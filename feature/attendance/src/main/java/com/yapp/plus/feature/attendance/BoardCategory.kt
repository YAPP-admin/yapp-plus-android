package com.yapp.plus.feature.attendance

import androidx.annotation.StringRes

enum class BoardCategory(
    @param:StringRes val labelResource: Int,
) {
    All(R.string.attendance_board_all),
    Session(R.string.attendance_board_session),
    Assignment(R.string.attendance_board_assignment),
    Other(R.string.attendance_board_other),
}
