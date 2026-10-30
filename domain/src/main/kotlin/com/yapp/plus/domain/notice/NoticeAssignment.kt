package com.yapp.plus.domain.notice

import java.time.LocalDate

data class NoticeAssignment(
    val deadlineDate: LocalDate,
    val positions: List<String>,
    val submissionUrl: String,
    val submissionLinkText: String,
)
