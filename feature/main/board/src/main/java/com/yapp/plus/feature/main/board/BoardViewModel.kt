package com.yapp.plus.feature.main.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.yapp.plus.core.ui.BaseViewModel
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardEvent
import com.yapp.plus.feature.main.board.model.BoardUiState
import com.yapp.plus.feature.main.board.model.NoticeUiState
import com.yapp.plus.feature.main.board.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BoardViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
        private val repository: NoticeRepository,
    ) : BaseViewModel<BoardUiState, BoardEvent>(initialBoardState(savedState)) {
        private var listJob: Job? = null
        private var detailJob: Job? = null
        private var readJob: Job? = null
        private var noticeId: Long? = null
        private val cursors = mutableSetOf<String>()

        init {
            fetchNotices()
        }

        fun selectCategory(value: BoardCategory) {
            if (uiState.value.category == value) return
            savedState[CATEGORY_KEY] = value.name
            updateState { it.copy(category = value) }
            fetchNotices()
        }

        fun fetchNotices() {
            listJob?.cancel()
            val category = uiState.value.category
            cursors.clear()
            updateState {
                it.copy(
                    notices = NoticeUiState.Loading,
                    nextCursor = null,
                    hasLoadMoreError = false,
                    isLoadingMore = false,
                )
            }
            listJob =
                viewModelScope.launch {
                    try {
                        val page = repository.fetchNotices(category.toNoticeType())
                        if (uiState.value.category != category) return@launch
                        val items = page.items.distinctBy { it.id }.map { it.toUiModel() }
                        updateState {
                            it.copy(
                                notices =
                                    if (items.isEmpty()) {
                                        NoticeUiState.Empty
                                    } else {
                                        NoticeUiState.Content(items)
                                    },
                                nextCursor = page.nextCursor,
                            )
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        if (uiState.value.category == category) {
                            updateState { it.copy(notices = NoticeUiState.Error) }
                        }
                    }
                }
        }

        fun fetchNextPage() {
            val state = uiState.value
            val current = (state.notices as? NoticeUiState.Content)?.value ?: return
            val cursor = state.nextCursor ?: return
            if (listJob?.isActive == true) return
            updateState { it.copy(isLoadingMore = true, hasLoadMoreError = false) }
            listJob =
                viewModelScope.launch {
                    try {
                        val page = repository.fetchNotices(state.category.toNoticeType(), cursor)
                        if (uiState.value.category != state.category) return@launch
                        check(page.nextCursor != cursor && page.nextCursor !in cursors)
                        cursors.add(cursor)
                        val items =
                            (current + page.items.map { it.toUiModel() })
                                .associateBy { it.id }
                                .values
                                .toList()
                        updateState {
                            it.copy(
                                notices = NoticeUiState.Content(items),
                                nextCursor = page.nextCursor,
                                isLoadingMore = false,
                            )
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        if (uiState.value.category == state.category) {
                            updateState {
                                it.copy(
                                    isLoadingMore = false,
                                    hasLoadMoreError = true,
                                )
                            }
                        }
                    }
                }
        }

        fun showNotice(id: Long?) {
            if (id == noticeId) return
            detailJob?.cancel()
            readJob?.cancel()
            noticeId = id
            updateState {
                it.copy(
                    detail = null,
                    isMarkingRead = false,
                )
            }
            if (id != null) fetchDetail()
        }

        fun retry() {
            if (noticeId == null) fetchNotices() else fetchDetail()
        }

        private fun fetchDetail() {
            val id = noticeId ?: return
            detailJob?.cancel()
            updateState { it.copy(detail = NoticeUiState.Loading) }
            detailJob =
                viewModelScope.launch {
                    try {
                        val notice = repository.fetchNotice(id).toUiModel()
                        if (noticeId == id) {
                            updateState { it.copy(detail = NoticeUiState.Content(notice)) }
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        if (noticeId == id) {
                            updateState { it.copy(detail = NoticeUiState.Error) }
                        }
                    }
                }
        }

        fun markRead() {
            val id = noticeId ?: return
            val notice = (uiState.value.detail as? NoticeUiState.Content)?.value ?: return
            if (notice.isRead || readJob?.isActive == true) return
            updateState { it.copy(isMarkingRead = true) }
            readJob =
                viewModelScope.launch {
                    try {
                        repository.markRead(id)
                        ensureActive()
                        if (noticeId != id) return@launch
                        updateState {
                            it.copy(
                                detail = NoticeUiState.Content(notice.copy(isRead = true)),
                            )
                        }
                        val refreshed = repository.fetchNotice(id).toUiModel()
                        ensureActive()
                        if (noticeId == id) {
                            updateState {
                                it.copy(
                                    detail = NoticeUiState.Content(refreshed),
                                )
                            }
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        ensureActive()
                        if (noticeId == id) sendEvent(BoardEvent.ReadFailed(id))
                    } finally {
                        if (noticeId == id) updateState { it.copy(isMarkingRead = false) }
                    }
                }
        }

        companion object {
            const val CATEGORY_KEY = "boardCategory"
        }
    }

private fun initialBoardState(savedState: SavedStateHandle): BoardUiState {
    val savedCategory = savedState.get<String>(BoardViewModel.CATEGORY_KEY)
    val category = BoardCategory.entries.find { it.name == savedCategory } ?: BoardCategory.All
    return BoardUiState(category = category)
}
