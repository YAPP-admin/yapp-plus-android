package com.yapp.plus.data.notice

import com.yapp.plus.domain.notice.NoticeAssignment
import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticeImage
import com.yapp.plus.domain.notice.NoticePage
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import java.net.URI
import java.time.Instant
import java.time.LocalDate

class NoticeRepositoryImpl(
    private val source: NoticeDataSource,
) : NoticeRepository {
    override suspend fun fetchNotices(
        type: NoticeType?,
        cursor: String?,
    ): NoticePage {
        val page = source.fetchNotices(type?.name ?: "ALL", cursor)
        require(
            page.totalPostCount >= 0 && (page.nextCursor == null || page.nextCursor.isNotBlank()),
        )
        return NoticePage(page.totalPostCount, page.items.map { it.toDomain() }, page.nextCursor)
    }

    override suspend fun fetchNotice(id: Long): NoticeDetail {
        require(id > 0)
        val detail = source.fetchNotice(id)
        require(detail.postId == id && detail.title.isNotBlank() && detail.readCount >= 0)
        return NoticeDetail(
            id,
            detail.title,
            detail.contentMarkdown,
            NoticeType.valueOf(detail.type),
            LocalDate.parse(detail.createdDate),
            detail.assignment?.toDomain(),
            detail.images.sortedBy { it.displayOrder }.map { it.toDomain() },
            detail.readCount,
            detail.read,
        )
    }

    override suspend fun markRead(id: Long) {
        require(id > 0)
        source.markRead(id)
    }
}

private fun NoticeSummaryData.toDomain(): NoticeSummary {
    require(postId > 0 && title.isNotBlank())
    return NoticeSummary(
        postId,
        title,
        NoticeType.valueOf(type),
        LocalDate.parse(createdDate),
        daysUntilDeadline,
    )
}

private fun NoticeAssignmentData.toDomain(): NoticeAssignment =
    NoticeAssignment(
        LocalDate.parse(deadlineDate),
        positions,
        validatedWebUrl(submissionUrl),
        submissionLinkText,
    )

private fun NoticeImageData.toDomain(): NoticeImage =
    NoticeImage(
        imageKey,
        filename,
        validatedWebUrl(readUrl),
        Instant.parse(readUrlExpiresAt),
    )

private fun validatedWebUrl(value: String): String {
    val uri = URI(value)
    require(uri.scheme in setOf("https", "http") && !uri.host.isNullOrBlank())
    return value
}
