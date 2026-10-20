package com.yapp.plus.feature.main.home.model

data class HomeInteractionUiState(
    val sheet: HomeSheet = HomeSheet.None,
    val code: String = "",
    val isSubmitting: Boolean = false,
    val errorResource: Int? = null,
    val isInvalidCode: Boolean = false,
    val attendedSessionId: Long? = null,
    val isAttendanceComplete: Boolean = false,
)
