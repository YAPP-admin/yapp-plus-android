package com.yapp.plus.feature.main.schedule

import com.yapp.plus.core.ui.BaseViewModel
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import com.yapp.plus.feature.main.schedule.model.SessionScheduleUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
internal class ScheduleViewModel
    @Inject
    constructor(
        private val sessionScheduleRepository: SessionScheduleRepository,
    ) : BaseViewModel<ScheduleUiState<List<ScheduleSessionUiModel>>, Nothing>(
            ScheduleUiState.Loading,
        ) {
        private var scheduleJob: Job? = null

        init {
            retry()
        }

        fun retry() {
            if (scheduleJob?.isActive == true) return
            scheduleJob =
                launchRequest(
                    request = sessionScheduleRepository::fetchCurrentGenerationSchedule,
                    onStart = { ScheduleUiState.Loading },
                    onSuccess = { _, schedule -> SessionScheduleUiMapper.toUiState(schedule) },
                    onFailure = { _, reason ->
                        when (reason) {
                            ScheduleFailure.NoActiveGeneration -> ScheduleUiState.Inactive
                            ScheduleFailure.Unavailable -> ScheduleUiState.Error
                        }
                    },
                )
        }
    }
