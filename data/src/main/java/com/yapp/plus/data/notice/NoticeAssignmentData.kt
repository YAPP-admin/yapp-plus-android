package com.yapp.plus.data.notice

data class NoticeAssignmentData(
    val deadlineDate: String,
    val positions: List<String>,
    val submissionUrl: String,
    val submissionLinkText: String,
)
