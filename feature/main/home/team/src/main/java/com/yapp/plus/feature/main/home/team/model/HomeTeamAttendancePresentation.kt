package com.yapp.plus.feature.main.home.team.model

import com.yapp.plus.core.ui.formatter.AppDateFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val teamZone: ZoneId = ZoneId.of("Asia/Seoul")
val teamTimeFormat: DateTimeFormatter get() = AppDateFormatter.timeHourMinute
val homeSessionStartFormat: DateTimeFormatter get() = AppDateFormatter.dayOfMonthTime
