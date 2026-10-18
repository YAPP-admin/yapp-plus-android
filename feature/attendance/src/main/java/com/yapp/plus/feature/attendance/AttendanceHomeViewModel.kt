package com.yapp.plus.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.attendance.model.AttendanceUiState
import com.yapp.plus.feature.attendance.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.attendance.model.SessionScheduleUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AttendanceHomeViewModel
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
                    mutableUiState.value = SessionScheduleUiMapper.toCurrentWeekUiState(schedule)
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (_: Exception) {
                    mutableUiState.value = AttendanceUiState.Error
                }
            }
        }
    }
