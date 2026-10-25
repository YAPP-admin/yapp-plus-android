package com.yapp.plus.feature.main.board.model

data class BoardNoticeUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val category: BoardCategory,
    val date: String,
)
