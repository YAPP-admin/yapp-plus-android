package com.yapp.plus.core.designsystem.component.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yapp.plus.core.designsystem.R

internal enum class BottomNavigationPreviewItem(
    @param:StringRes val labelResource: Int,
    @param:DrawableRes val iconResource: Int,
) {
    Home(R.string.yapp_preview_navigation_home, R.drawable.ic_navigation_house),
    Schedule(R.string.yapp_preview_navigation_schedule, R.drawable.ic_navigation_calendar),
    Board(R.string.yapp_preview_navigation_board, R.drawable.ic_navigation_presentation),
    My(R.string.yapp_preview_navigation_my, R.drawable.ic_navigation_user),
}
