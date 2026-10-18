package com.yapp.plus.feature.main.schedule.model

sealed interface ScheduleUiState<out T> {
    data object Loading : ScheduleUiState<Nothing>

    data object Empty : ScheduleUiState<Nothing>

    data object Error : ScheduleUiState<Nothing>

    data class Content<T>(
        val value: T,
    ) : ScheduleUiState<T>
}
