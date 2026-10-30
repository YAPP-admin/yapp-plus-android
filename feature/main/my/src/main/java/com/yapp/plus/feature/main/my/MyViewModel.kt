package com.yapp.plus.feature.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.auth.UserProfileRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyViewModel
    @Inject
    constructor(
        private val repository: AttendanceReadRepository,
        private val profileRepository: UserProfileRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(MyUiState())
        val uiState = mutableState.asStateFlow()
        private var scoreJob: Job? = null
        private var historyJob: Job? = null
        private val loadedCursors = mutableSetOf<String>()

        fun fetchProfile() {
            viewModelScope.launch {
                val name =
                    try {
                        profileRepository.fetchName()
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        null
                    }
                mutableState.update {
                    it.copy(
                        profile =
                            name?.let { MyProfileUiState.Content(MyProfileUiModel(it)) }
                                ?: MyProfileUiState.Unavailable,
                    )
                }
            }
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
            if (historyJob?.isActive == true) return
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
                        updateHistory(current)
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
