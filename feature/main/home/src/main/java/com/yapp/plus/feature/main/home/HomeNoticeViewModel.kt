package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.feature.main.home.model.HomeNoticeUiModel
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
internal class HomeNoticeViewModel
    @Inject
    constructor(
        private val repository: NoticeRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow<HomeNoticeUiState>(HomeNoticeUiState.Loading)
        val uiState = mutableState.asStateFlow()
        private var job: Job? = null

        fun fetchNotices() {
            if (job?.isActive == true) return
            job =
                viewModelScope.launch {
                    mutableState.value = HomeNoticeUiState.Loading
                    try {
                        val start =
                            LocalDate
                                .now(ZoneId.of("Asia/Seoul"))
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                        val items =
                            repository
                                .fetchNotices()
                                .items
                                .filter { it.createdDate in start..start.plusDays(6) }
                                .take(3)
                                .map {
                                    HomeNoticeUiModel(
                                        it.id,
                                        it.title,
                                        isSessionNotice =
                                            it.type == NoticeType.SESSION,
                                        isAssignmentNotice =
                                            it.type == NoticeType.ASSIGNMENT,
                                    )
                                }
                        mutableState.value =
                            if (items.isEmpty()) {
                                HomeNoticeUiState.Empty
                            } else {
                                HomeNoticeUiState.Content(
                                    items,
                                )
                            }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        mutableState.value = HomeNoticeUiState.Error
                    }
                }
        }
    }
