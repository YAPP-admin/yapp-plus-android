package com.yapp.plus.feature.main.board.model

internal sealed interface BoardEvent {
    data class ReadFailed(
        val noticeId: Long,
    ) : BoardEvent
}
