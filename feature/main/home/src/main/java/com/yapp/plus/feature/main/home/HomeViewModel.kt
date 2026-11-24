package com.yapp.plus.feature.main.home

import androidx.lifecycle.viewModelScope
import com.yapp.plus.core.ui.BaseViewModel
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.domain.session.ScheduleFailure
import com.yapp.plus.domain.session.SessionScheduleRepository
import com.yapp.plus.feature.main.home.model.AttendanceStatusUiModel
import com.yapp.plus.feature.main.home.model.AttendanceUiState
import com.yapp.plus.feature.main.home.model.CurrentWeekSessionUiMapper
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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

        fun loadHomeData() {
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
            if (scheduleJob?.isActive == true) return
            scheduleJob =
                launchRequest(
                    request = sessionScheduleRepository::fetchCurrentWeekSchedule,
                    onStart = { it.copy(currentWeekSchedule = AttendanceUiState.Loading) },
                    onSuccess = { state, schedule ->
                        val scheduleState = CurrentWeekSessionUiMapper.toUiState(schedule)
                        state.copy(currentWeekSchedule = scheduleState)
                    },
                    onFailure = { state, reason ->
                        val schedule =
                            when (reason) {
                                ScheduleFailure.NoActiveGeneration -> AttendanceUiState.Inactive
                                ScheduleFailure.Unavailable -> AttendanceUiState.Error
                            }
                        state.copy(currentWeekSchedule = schedule)
                    },
                )
        }

        fun retryWeeklyNotices() {
            if (noticesJob?.isActive == true) return
            noticesJob =
                viewModelScope.launch {
                    updateState { it.copy(weeklyNotices = HomeNoticeUiState.Loading) }
                    fetchWeeklyNotices()
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
                updateState {
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
                updateState { it.copy(weeklyNotices = HomeNoticeUiState.Error) }
            }
        }
    }
