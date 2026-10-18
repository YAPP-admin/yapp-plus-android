package com.yapp.plus.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.attendance.model.AttendanceScheduleSessionUiModel
import com.yapp.plus.feature.attendance.model.AttendanceUiState
import com.yapp.plus.feature.attendance.model.SessionScheduleUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AttendanceScheduleViewModel
    @Inject
    constructor(
        private val sessionScheduleRepository: SessionScheduleRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow<AttendanceUiState<List<AttendanceScheduleSessionUiModel>>>(
                AttendanceUiState.Loading,
            )
        val uiState = mutableUiState.asStateFlow()

        init {
            fetchCurrentGenerationSchedule()
        }

        fun retry() {
            fetchCurrentGenerationSchedule()
        }

        private fun fetchCurrentGenerationSchedule() {
            viewModelScope.launch {
                mutableUiState.value = AttendanceUiState.Loading
                try {
                    val schedule = sessionScheduleRepository.fetchCurrentGenerationSchedule()
                    mutableUiState.value = SessionScheduleUiMapper.toScheduleUiState(schedule)
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (_: Exception) {
                    mutableUiState.value = AttendanceUiState.Error
                }
            }
        }
    }
