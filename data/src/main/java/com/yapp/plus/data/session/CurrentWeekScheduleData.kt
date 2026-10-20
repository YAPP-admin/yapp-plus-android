package com.yapp.plus.data.session

import java.time.LocalDate

data class CurrentWeekScheduleData(
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val session: CurrentWeekSessionData?,
)
