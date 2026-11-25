package com.yapp.plus.domain.afterparty

data class AfterpartySummary(
    val id: Long,
    val title: String,
    val schedule: AfterpartySchedule,
    val state: AfterpartyState,
    val respondedCount: Int,
)
