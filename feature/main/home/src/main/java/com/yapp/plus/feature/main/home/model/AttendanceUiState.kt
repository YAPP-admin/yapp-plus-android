package com.yapp.plus.feature.main.home.model

sealed interface AttendanceUiState<out T> {
    data object Loading : AttendanceUiState<Nothing>

    data object Empty : AttendanceUiState<Nothing>

    data object Error : AttendanceUiState<Nothing>

    data class Content<T>(
        val value: T,
    ) : AttendanceUiState<T>
}
