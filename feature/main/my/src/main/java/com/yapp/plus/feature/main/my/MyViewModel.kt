package com.yapp.plus.feature.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.auth.UserProfileRepository
import com.yapp.plus.domain.profile.UserProfileReadRepository
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyAttendanceScoreState
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import com.yapp.plus.feature.main.my.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyViewModel
    @Inject
    constructor(
        private val repository: AttendanceReadRepository,
        private val savedProfileRepository: UserProfileRepository,
        private val profileRepository: UserProfileReadRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(MyUiState(isLoading = true))
        val uiState = mutableState.asStateFlow()
        private var initialLoadJob: Job? = null
        private var profileJob: Job? = null
        private var scoreJob: Job? = null
        private var historyJob: Job? = null
        private val loadedCursors = mutableSetOf<String>()

        fun loadInitialData(loadHistory: Boolean) {
            initialLoadJob?.cancel()
            mutableState.update { it.copy(isLoading = true) }
            fetchProfile()
            fetchScore()
            if (loadHistory) fetchHistory()
            val requests = listOfNotNull(profileJob, scoreJob, historyJob.takeIf { loadHistory })
            initialLoadJob =
                viewModelScope.launch {
                    requests.joinAll()
                    mutableState.update { it.copy(isLoading = false) }
                }
        }

        fun fetchProfile() {
            if (profileJob?.isActive == true) return
            profileJob =
                viewModelScope.launch {
                    updateProfile(MyProfileUiState.Loading())
                    val savedName = fetchSavedName()
                    updateProfile(MyProfileUiState.Loading(savedName))
                    try {
                        val profile = profileRepository.fetchProfile()
                        updateProfile(MyProfileUiState.Content(MyProfileUiModel.from(profile)))
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        updateProfile(MyProfileUiState.Error(savedName))
                    }
                }
        }

        fun cancelProfileRequest() {
            profileJob?.cancel()
            profileJob = null
        }

        fun cancelInitialLoad() {
            initialLoadJob?.cancel()
            initialLoadJob = null
            cancelProfileRequest()
        }

        private suspend fun fetchSavedName(): String? =
            try {
                savedProfileRepository.fetchName()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                null
            }

        private fun updateProfile(profile: MyProfileUiState) {
            mutableState.update { it.copy(profile = profile) }
        }

        fun fetchScore() {
            if (scoreJob?.isActive == true) return
            scoreJob =
                viewModelScope.launch {
                    mutableState.update { it.copy(score = MyAttendanceScoreState.Loading) }
                    try {
                        val score = repository.fetchScore()
                        mutableState.update {
                            it.copy(
                                score =
                                    score?.let { value -> MyAttendanceScoreState.Content(value) }
                                        ?: MyAttendanceScoreState.Ineligible,
                            )
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableState.update { it.copy(score = MyAttendanceScoreState.Error) }
                    }
                }
        }

        fun fetchHistory() {
            val history = mutableState.value.attendanceHistory
            val isPaging = history is MyAttendanceHistoryState.Content && history.isLoadingMore
            if (historyJob?.isActive == true && !isPaging) return
            historyJob?.cancel()
            historyJob =
                viewModelScope.launch {
                    updateHistory(MyAttendanceHistoryState.Loading)
                    try {
                        val page = repository.fetchHistory().toUiState()
                        loadedCursors.clear()
                        updateHistory(page)
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        updateHistory(MyAttendanceHistoryState.Error)
                    }
                }
        }

        fun fetchNextPage() {
            val current =
                mutableState.value.attendanceHistory as? MyAttendanceHistoryState.Content ?: return
            val cursor = current.nextCursor ?: return
            if (historyJob?.isActive == true) return
            historyJob =
                viewModelScope.launch {
                    updateHistory(current.copy(isLoadingMore = true, hasLoadMoreError = false))
                    try {
                        val page = repository.fetchHistory(cursor).toUiState()
                        check(page.nextCursor != cursor && page.nextCursor !in loadedCursors)
                        loadedCursors.add(cursor)
                        updateHistory(
                            page.copy(
                                items =
                                    (current.items + page.items)
                                        .associateBy { it.sessionId }
                                        .values
                                        .toList(),
                            ),
                        )
                    } catch (exception: CancellationException) {
                        if (historyJob == coroutineContext[Job]) updateHistory(current)
                        throw exception
                    } catch (_: Exception) {
                        updateHistory(current.copy(hasLoadMoreError = true))
                    }
                }
        }

        private fun updateHistory(history: MyAttendanceHistoryState) {
            mutableState.update { it.copy(attendanceHistory = history) }
        }
    }
