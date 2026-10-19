package com.yapp.plus.feature.main.schedule.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.main.schedule.R

enum class ScheduleStatusUiModel(
    @param:StringRes val labelResource: Int?,
    val isToday: Boolean = false,
    val isPast: Boolean = false,
) {
    Past(labelResource = null, isPast = true),
    Today(labelResource = R.string.schedule_today, isToday = true),
    Upcoming(labelResource = R.string.schedule_upcoming),
}
