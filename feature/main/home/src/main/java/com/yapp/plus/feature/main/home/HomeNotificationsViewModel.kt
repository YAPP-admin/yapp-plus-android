package com.yapp.plus.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.notification.Notification
import com.yapp.plus.domain.notification.NotificationRepository
import com.yapp.plus.feature.main.home.model.HomeNotificationUiModel
import com.yapp.plus.feature.main.home.model.HomeNotificationsUiState
import com.yapp.plus.feature.main.home.model.HomeNotificationsUiState.Content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class HomeNotificationsViewModel
    @Inject
    constructor(
        private val repository: NotificationRepository,
    ) : ViewModel() {
        private val state =
            MutableStateFlow<HomeNotificationsUiState>(HomeNotificationsUiState.Loading)
        val uiState = state.asStateFlow()
        private val consumedCursors = mutableSetOf<String>()
        private var request: Job? = null

        init {
            refresh()
        }

        fun refresh() {
            if (request?.isActive == true) return
            val previous = state.value as? Content
            state.value =
                previous?.copy(isRefreshing = true, hasRefreshError = false)
                    ?: HomeNotificationsUiState.Loading
            request =
                viewModelScope.launch {
                    try {
                        val page = repository.fetchNotifications()
                        consumedCursors.clear()
                        state.value = Content(page.items.toUniqueUiItems(), page.nextCursor)
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        state.value =
                            previous?.copy(hasRefreshError = true) ?: HomeNotificationsUiState.Error
                    }
                }
        }

        fun loadMore() {
            if (request?.isActive == true) return
            val previous = state.value as? Content ?: return
            val cursor = previous.nextCursor ?: return
            state.value = previous.copy(isLoadingMore = true, hasLoadMoreError = false)
            request =
                viewModelScope.launch {
                    try {
                        val page = repository.fetchNotifications(cursor)
                        check(page.nextCursor != cursor && page.nextCursor !in consumedCursors)
                        consumedCursors.add(cursor)
                        val items = (previous.items + page.items.toUniqueUiItems()).distinctBy { it.id }
                        state.value =
                            previous.copy(
                                items = items,
                                nextCursor = page.nextCursor,
                                hasLoadMoreError = false,
                            )
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        state.value = previous.copy(hasLoadMoreError = true)
                    }
                }
        }
    }

private val notificationDateFormat = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)

private fun List<Notification>.toUniqueUiItems(): List<HomeNotificationUiModel> =
    distinctBy { it.id }.map {
        HomeNotificationUiModel(
            id = it.id,
            message = it.content,
            dateLabel = it.sentDate.format(notificationDateFormat),
            postId = it.postId,
        )
    }
