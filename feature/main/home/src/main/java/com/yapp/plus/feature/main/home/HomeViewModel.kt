package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiMapper
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.home.model.toHomeNoticeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        private val sessionScheduleRepository: SessionScheduleRepository,
        private val noticeRepository: NoticeRepository,
    ) : ViewModel() {
        private val mutableUiState =
            MutableStateFlow<AttendanceUiState<CurrentWeekSessionUiModel>>(
                AttendanceUiState.Loading,
            )
        val uiState = mutableUiState.asStateFlow()
        private val mutableNotices = MutableStateFlow<HomeNoticeUiState>(HomeNoticeUiState.Loading)
        val notices = mutableNotices.asStateFlow()
        private var noticeRequest: Job? = null

        init {
            fetchCurrentWeekSchedule()
            retryNotices()
        }

        fun retry() {
            fetchCurrentWeekSchedule()
        }

        fun retryNotices() {
            if (noticeRequest?.isActive == true) return
            mutableNotices.value = HomeNoticeUiState.Loading
            noticeRequest =
                viewModelScope.launch {
                    try {
                        mutableNotices.value = noticeRepository.fetchNotices().toHomeNoticeState()
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableNotices.value = HomeNoticeUiState.Error
                    }
                }
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
