package com.yapp.plus.feature.main.home

import androidx.lifecycle.viewModelScope
import com.yapp.plus.core.ui.BaseViewModel
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiMapper
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    ) : BaseViewModel<HomeUiState>(HomeUiState()) {
        private var scheduleJob: Job? = null
        private var noticesJob: Job? = null
        private var refreshJob: Job? = null
        private var hasRequestedInitialHomeData = false

        fun loadInitialHomeData() {
            if (hasRequestedInitialHomeData) return
            hasRequestedInitialHomeData = true
            retryCurrentWeekSchedule()
            retryWeeklyNotices()
        }

        fun markAttendanceVerified(sessionId: Long) {
            updateState { state ->
                val session =
                    (state.currentWeekSchedule as? AttendanceUiState.Content)?.value
                        ?: return@updateState state
                if (session.sessionId != sessionId) return@updateState state
                if (session.attendanceStatus != AttendanceStatusUiModel.Available) {
                    return@updateState state
                }
                state.copy(
                    currentWeekSchedule =
                        AttendanceUiState.Content(
                            session.copy(attendanceStatus = AttendanceStatusUiModel.CheckedIn),
                        ),
                )
            }
        }

        fun retryCurrentWeekSchedule() {
            if (scheduleJob?.isActive == true || refreshJob?.isActive == true) return
            scheduleJob =
                launchRequest(
                    request = sessionScheduleRepository::fetchCurrentWeekSchedule,
                    onStart = { it.copy(currentWeekSchedule = AttendanceUiState.Loading) },
                    onSuccess = { state, schedule ->
                        val scheduleState = CurrentWeekSessionUiMapper.toUiState(schedule)
                        state.copy(currentWeekSchedule = scheduleState)
                    },
                    onFailure = { state, reason -> state.copy(currentWeekSchedule = scheduleState(reason)) },
                )
        }

        fun refreshHome() {
            if (uiState.value.isRefreshing || refreshJob?.isActive == true) return
            scheduleJob?.cancel()
            noticesJob?.cancel()
            refreshJob =
                viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true) }
                    try {
                        val (schedule, notices) =
                            coroutineScope {
                                val scheduleRequest = async { fetchCurrentWeekScheduleState() }
                                val noticesRequest = async { fetchWeeklyNotices() }
                                scheduleRequest.await() to noticesRequest.await()
                            }
                        updateState {
                            it.copy(
                                currentWeekSchedule = schedule,
                                weeklyNotices = notices,
                            )
                        }
                    } finally {
                        updateState { it.copy(isRefreshing = false) }
                    }
                }
        }

        fun retryWeeklyNotices() {
            if (noticesJob?.isActive == true || refreshJob?.isActive == true) return
            noticesJob =
                viewModelScope.launch {
                    updateState { it.copy(weeklyNotices = HomeNoticeUiState.Loading) }
                    val notices = fetchWeeklyNotices()
                    updateState { it.copy(weeklyNotices = notices) }
                }
        }

        private suspend fun fetchCurrentWeekScheduleState(): AttendanceUiState<CurrentWeekSessionUiModel> =
            try {
                when (val result = sessionScheduleRepository.fetchCurrentWeekSchedule()) {
                    is YappResult.Success -> CurrentWeekSessionUiMapper.toUiState(result.value)
                    is YappResult.Failure -> scheduleState(result.reason)
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                AttendanceUiState.Error
            }

        private fun scheduleState(reason: ScheduleFailure): AttendanceUiState<CurrentWeekSessionUiModel> =
            when (reason) {
                ScheduleFailure.NoActiveGeneration -> AttendanceUiState.Inactive
                ScheduleFailure.Unavailable -> AttendanceUiState.Error
            }

        private suspend fun fetchWeeklyNotices(): HomeNoticeUiState =
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
                if (items.isEmpty()) HomeNoticeUiState.Empty else HomeNoticeUiState.Content(items)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                HomeNoticeUiState.Error
            }
    }
