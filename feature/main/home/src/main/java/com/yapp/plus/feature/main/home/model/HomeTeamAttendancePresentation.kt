package com.yapp.plus.feature.main.home.model

import com.yapp.plus.core.ui.formatter.AppDateFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal val teamZone: ZoneId = ZoneId.of("Asia/Seoul")
internal val teamTimeFormat: DateTimeFormatter get() = AppDateFormatter.timeHourMinute
