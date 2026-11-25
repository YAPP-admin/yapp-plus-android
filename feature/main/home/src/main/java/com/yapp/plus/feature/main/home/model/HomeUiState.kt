package com.yapp.plus.feature.main.home.model

data class HomeUiState(
    val currentWeekSchedule: AttendanceUiState<CurrentWeekSessionUiModel> = AttendanceUiState.Loading,
    val weeklyNotices: HomeNoticeUiState = HomeNoticeUiState.Loading,
    val isRefreshing: Boolean = false,
) {
    val isLoading: Boolean
        get() =
            currentWeekSchedule == AttendanceUiState.Loading ||
                weeklyNotices == HomeNoticeUiState.Loading
}
