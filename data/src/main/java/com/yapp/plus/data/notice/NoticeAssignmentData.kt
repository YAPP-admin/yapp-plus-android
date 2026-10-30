package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeAssignment
import java.time.LocalDate

data class NoticeAssignmentData(
    val deadlineDate: String,
    val positions: List<String>,
    val submissionUrl: String,
    val submissionLinkText: String,
) {
    internal fun toDomain(): NoticeAssignment =
        NoticeAssignment(
            LocalDate.parse(deadlineDate),
            positions,
            validatedNoticeWebUrl(submissionUrl),
            submissionLinkText,
        )
}
