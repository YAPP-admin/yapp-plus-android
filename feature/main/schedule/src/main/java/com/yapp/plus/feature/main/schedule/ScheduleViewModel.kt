package com.yapp.plus.feature.main.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import com.yapp.plus.feature.main.schedule.model.SessionScheduleUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ScheduleViewModel
    @Inject
    constructor(
        private val sessionScheduleRepository: SessionScheduleRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow<ScheduleUiState<List<ScheduleSessionUiModel>>>(
                ScheduleUiState.Loading,
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
                mutableUiState.value = ScheduleUiState.Loading
                try {
                    val schedule = sessionScheduleRepository.fetchCurrentGenerationSchedule()
                    mutableUiState.value = SessionScheduleUiMapper.toUiState(schedule)
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (_: Exception) {
                    mutableUiState.value = ScheduleUiState.Error
                }
            }
        }
    }
