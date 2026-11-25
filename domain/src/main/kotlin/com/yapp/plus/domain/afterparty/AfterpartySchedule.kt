package com.yapp.plus.domain.afterparty

import java.time.Instant

data class AfterpartySchedule(
    val startsAt: Instant,
    val startsAtDisplay: String,
    val deadline: Instant,
    val deadlineDisplay: String,
)
