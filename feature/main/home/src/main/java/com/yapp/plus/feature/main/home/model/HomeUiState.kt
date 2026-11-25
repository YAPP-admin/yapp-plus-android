package com.yapp.plus.feature.main.home.model

import com.yapp.plus.core.ui.model.AttendanceUiState
import com.yapp.plus.core.ui.model.CurrentWeekSessionUiModel

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
