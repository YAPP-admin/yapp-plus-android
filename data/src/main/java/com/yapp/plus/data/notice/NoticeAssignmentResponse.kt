package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticeAssignmentResponse(
    val deadlineDate: String,
    val positions: List<String>,
    val submissionUrl: String,
    val submissionLinkText: String,
)
