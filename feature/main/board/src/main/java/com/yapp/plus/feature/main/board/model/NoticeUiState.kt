package com.yapp.plus.feature.main.board.model

sealed interface NoticeUiState<out T> {
    data object Unavailable : NoticeUiState<Nothing>

    data object Loading : NoticeUiState<Nothing>

    data object Error : NoticeUiState<Nothing>

    data object Empty : NoticeUiState<Nothing>

    data class Content<T>(
        val value: T,
    ) : NoticeUiState<T>
}
