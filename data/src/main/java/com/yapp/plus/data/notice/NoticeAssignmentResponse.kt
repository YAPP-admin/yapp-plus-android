package com.yapp.plus.data.notice

import kotlinx.serialization.Serializable

@Serializable
internal data class NoticeAssignmentResponse(
    val deadlineDate: String,
    val positions: List<String>,
    val submissionUrl: String,
    val submissionLinkText: String,
) {
    fun toData(): NoticeAssignmentData =
        NoticeAssignmentData(
            deadlineDate = deadlineDate,
            positions = positions,
            submissionUrl = submissionUrl,
            submissionLinkText = submissionLinkText,
        )
}
