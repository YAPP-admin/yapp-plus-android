package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiMapper
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        private val sessionScheduleRepository: SessionScheduleRepository,
        private val noticeRepository: NoticeRepository,
    ) : ViewModel() {
        private val mutableUiState = MutableStateFlow(HomeUiState())
        val uiState = mutableUiState.asStateFlow()
        private var scheduleJob: Job? = null
        private var noticesJob: Job? = null

        fun loadHomeData() {
            if (uiState.value.currentWeekSchedule == AttendanceUiState.Loading) {
                retryCurrentWeekSchedule()
            }
            retryWeeklyNotices()
        }

        fun retryCurrentWeekSchedule() {
            if (scheduleJob?.isActive == true) return
            scheduleJob =
                viewModelScope.launch {
                    mutableUiState.update { it.copy(currentWeekSchedule = AttendanceUiState.Loading) }
                    fetchCurrentWeekSchedule()
                }
        }

        fun retryWeeklyNotices() {
            if (noticesJob?.isActive == true) return
            noticesJob =
                viewModelScope.launch {
                    mutableUiState.update { it.copy(weeklyNotices = HomeNoticeUiState.Loading) }
                    fetchWeeklyNotices()
                }
        }

        private suspend fun fetchCurrentWeekSchedule() {
            try {
                val schedule = sessionScheduleRepository.fetchCurrentWeekSchedule()
                mutableUiState.update {
                    it.copy(currentWeekSchedule = CurrentWeekSessionUiMapper.toUiState(schedule))
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                mutableUiState.update { it.copy(currentWeekSchedule = AttendanceUiState.Error) }
            }
        }

        private suspend fun fetchWeeklyNotices() {
            try {
                val weekStart =
                    LocalDate
                        .now(ZoneId.of("Asia/Seoul"))
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                val items =
                    noticeRepository
                        .fetchNotices()
                        .items
                        .filter { it.createdDate in weekStart..weekStart.plusDays(6) }
                        .take(3)
                        .map {
                            HomeNoticeUiModel(
                                id = it.id,
                                title = it.title,
                                isSessionNotice = it.type == NoticeType.SESSION,
                                isAssignmentNotice = it.type == NoticeType.ASSIGNMENT,
                            )
                        }
                mutableUiState.update {
                    it.copy(
                        weeklyNotices =
                            if (items.isEmpty()) {
                                HomeNoticeUiState.Empty
                            } else {
                                HomeNoticeUiState.Content(items)
                            },
                    )
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                mutableUiState.update { it.copy(weeklyNotices = HomeNoticeUiState.Error) }
            }
        }
    }
