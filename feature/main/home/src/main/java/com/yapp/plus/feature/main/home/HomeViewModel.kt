package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiMapper
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        private val sessionScheduleRepository: SessionScheduleRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow<AttendanceUiState<CurrentWeekSessionUiModel>>(
                AttendanceUiState.Loading,
            )
        val uiState = mutableUiState.asStateFlow()

        init {
            fetchCurrentWeekSchedule()
        }

        fun retry() {
            fetchCurrentWeekSchedule()
        }

        private fun fetchCurrentWeekSchedule() {
            viewModelScope.launch {
                mutableUiState.value = AttendanceUiState.Loading
                try {
                    val schedule = sessionScheduleRepository.fetchCurrentWeekSchedule()
                    mutableUiState.value = CurrentWeekSessionUiMapper.toUiState(schedule)
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (_: Exception) {
                    mutableUiState.value = AttendanceUiState.Error
                }
            }
        }
    }
