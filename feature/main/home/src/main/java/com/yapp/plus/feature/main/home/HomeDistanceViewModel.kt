package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.session.SessionCoordinates
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.domain.session.SessionDistanceRepository
import com.yapp.plus.feature.main.home.model.SessionDistanceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeDistanceViewModel
    @Inject
    constructor(
        private val repository: SessionDistanceRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow<SessionDistanceUiState>(SessionDistanceUiState.Idle)
        val uiState = mutableUiState.asStateFlow()
        private var measurement: Job? = null

        fun measure(coordinates: SessionCoordinates) {
            if (measurement?.isActive == true) return
            mutableUiState.value = SessionDistanceUiState.Loading
            measurement =
                viewModelScope.launch {
                    val distance = fetchDistance(coordinates)
                    ensureActive()
                    mutableUiState.value = SessionDistanceUiState.Result(distance)
                }
        }

        fun onPermissionDenied() {
            mutableUiState.value = SessionDistanceUiState.Result(SessionDistance.PermissionRequired)
        }

        fun cancelMeasurement() {
            measurement?.cancel()
            measurement = null
            if (uiState.value == SessionDistanceUiState.Loading) {
                mutableUiState.value = SessionDistanceUiState.Idle
            }
        }

        fun clear() {
            cancelMeasurement()
            mutableUiState.value = SessionDistanceUiState.Idle
        }

        private suspend fun fetchDistance(coordinates: SessionCoordinates): SessionDistance =
            try {
                repository.fetchDistanceTo(coordinates)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                SessionDistance.Unavailable
            }
    }
