package com.yapp.plus.domain.afterparty

import java.time.Instant

data class AfterpartyDetail(
    val id: Long,
    val title: String,
    val content: String,
    val schedule: AfterpartySchedule,
    val state: AfterpartyState,
    val response: AfterpartyResponse,
    val attendingCount: Int?,
) {
    fun canVote(now: Instant): Boolean = state == AfterpartyState.OPEN && now.isBefore(schedule.deadline)
}
