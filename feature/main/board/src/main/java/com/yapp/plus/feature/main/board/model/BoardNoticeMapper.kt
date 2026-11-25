package com.yapp.plus.feature.main.board.model

import com.yapp.plus.core.ui.formatter.AppDateFormatter
import com.yapp.plus.domain.notice.NoticeDetail
import com.yapp.plus.domain.notice.NoticeSummary
import com.yapp.plus.domain.notice.NoticeType
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

internal object BoardNoticeMapper {
    fun toUiModel(summary: NoticeSummary): BoardNoticeUiModel =
        BoardNoticeUiModel(
            id = summary.id,
            title = summary.title,
            content = "",
            category = summary.type.toCategory(),
            date = summary.createdDate.toNoticeLabel(),
            daysUntilDeadline = summary.daysUntilDeadline,
        )

    fun toUiModel(detail: NoticeDetail): BoardNoticeUiModel =
        BoardNoticeUiModel(
            id = detail.id,
            title = detail.title,
            content = detail.contentMarkdown,
            category = detail.type.toCategory(),
            date = detail.createdDate.toNoticeLabel(),
            daysUntilDeadline =
                detail.assignment?.let {
                    ChronoUnit.DAYS.between(LocalDate.now(ZoneId.of("Asia/Seoul")), it.deadlineDate)
                },
            assignment = detail.assignment,
            images = detail.images,
            readCount = detail.readCount,
            isRead = detail.isRead,
        )

    private fun NoticeType.toCategory(): BoardCategory =
        when (this) {
            NoticeType.SESSION -> BoardCategory.Session
            NoticeType.ASSIGNMENT -> BoardCategory.Assignment
            NoticeType.OPERATION -> BoardCategory.Operation
        }

    private fun LocalDate.toNoticeLabel(): String = format(AppDateFormatter.yearMonthDayDotDayOfWeek)
}

internal fun NoticeSummary.toUiModel(): BoardNoticeUiModel = BoardNoticeMapper.toUiModel(this)

internal fun NoticeDetail.toUiModel(): BoardNoticeUiModel = BoardNoticeMapper.toUiModel(this)
