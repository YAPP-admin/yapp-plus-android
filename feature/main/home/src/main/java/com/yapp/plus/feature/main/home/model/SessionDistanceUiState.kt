package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.session.SessionDistance

internal sealed interface SessionDistanceUiState {
    data object Idle : SessionDistanceUiState

    data object Loading : SessionDistanceUiState

    data class Result(
        val distance: SessionDistance,
    ) : SessionDistanceUiState
}
