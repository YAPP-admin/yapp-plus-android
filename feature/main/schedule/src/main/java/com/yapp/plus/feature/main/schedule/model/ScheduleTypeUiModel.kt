package com.yapp.plus.feature.main.schedule.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.main.schedule.R

enum class ScheduleTypeUiModel(
    @param:StringRes val labelResource: Int,
) {
    Regular(R.string.schedule_regular),
    Team(R.string.schedule_team),
}
