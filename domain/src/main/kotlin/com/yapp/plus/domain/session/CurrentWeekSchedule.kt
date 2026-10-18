package com.yapp.plus.domain.session

import java.time.LocalDate

data class CurrentWeekSchedule(
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val session: CurrentWeekSession?,
)
