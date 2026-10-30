package com.yapp.plus.feature.main.board.model

import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

internal fun BoardCategory.toNoticeType(): NoticeType? =
    when (this) {
        BoardCategory.All -> null
        BoardCategory.Session -> NoticeType.SESSION
        BoardCategory.Assignment -> NoticeType.ASSIGNMENT
        BoardCategory.Operation -> NoticeType.OPERATION
    }

private fun NoticeType.toCategory(): BoardCategory =
    when (this) {
        NoticeType.SESSION -> BoardCategory.Session
        NoticeType.ASSIGNMENT -> BoardCategory.Assignment
        NoticeType.OPERATION -> BoardCategory.Operation
    }

private val noticeDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN)

private fun LocalDate.toNoticeLabel(): String = format(noticeDateFormatter)

internal fun NoticeSummary.toUiModel() =
    BoardNoticeUiModel(
        id,
        title,
        "",
        type.toCategory(),
        createdDate.toNoticeLabel(),
        daysUntilDeadline,
    )

internal fun NoticeDetail.toUiModel() =
    BoardNoticeUiModel(
        id,
        title,
        contentMarkdown,
        type.toCategory(),
        createdDate.toNoticeLabel(),
        assignment?.let {
            ChronoUnit.DAYS.between(LocalDate.now(ZoneId.of("Asia/Seoul")), it.deadlineDate)
        },
        assignment,
        images,
        readCount,
        isRead,
    )
