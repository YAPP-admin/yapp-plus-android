package com.yapp.plus.feature.main.board.model

import com.yapp.plus.domain.notice.Notice
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class BoardNoticeUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val category: BoardCategory,
    val date: String,
)

internal fun Notice.toUiModel() =
    BoardNoticeUiModel(
        id = id,
        title = title,
        content = content,
        category = BoardCategory.entries.first { it.noticeType == type },
        date =
            publishedAt.atZone(ZoneId.of("Asia/Seoul")).format(
                DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN),
            ),
    )
