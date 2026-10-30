package com.yapp.plus.domain.notice

import java.time.LocalDate

data class NoticeSummary(
    val id: Long,
    val title: String,
    val type: NoticeType,
    val createdDate: LocalDate,
    val daysUntilDeadline: Long?,
)
