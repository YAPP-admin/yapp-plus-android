package com.yapp.plus.feature.attendance

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yapp.plus.core.designsystem.R as DesignSystemR

enum class AttendanceTab(
    @param:StringRes val labelResource: Int,
    @param:DrawableRes val iconResource: Int,
) {
    Home(R.string.attendance_tab_home, DesignSystemR.drawable.ic_navigation_house),
    Schedule(R.string.attendance_tab_schedule, DesignSystemR.drawable.ic_navigation_calendar),
    Board(R.string.attendance_tab_board, DesignSystemR.drawable.ic_navigation_presentation),
    My(R.string.attendance_tab_my, DesignSystemR.drawable.ic_navigation_user),
}
