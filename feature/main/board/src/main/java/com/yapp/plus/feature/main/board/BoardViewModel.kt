package com.yapp.plus.feature.main.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState
import com.yapp.plus.feature.main.board.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BoardViewModel
    @Inject
    constructor(
        private val repository: NoticeRepository,
        private val savedState: SavedStateHandle,
    ) : ViewModel() {
        private val mutableCategory =
            MutableStateFlow(
                BoardCategory.entries.find { it.name == savedState.get<String>(CATEGORY_KEY) }
                    ?: BoardCategory.All,
            )
        val category = mutableCategory.asStateFlow()
        private val mutableNotices =
            MutableStateFlow<NoticeUiState<List<BoardNoticeUiModel>>>(
                NoticeUiState.Loading,
            )
        val notices = mutableNotices.asStateFlow()
        private val mutableDetail =
            MutableStateFlow<NoticeUiState<BoardNoticeUiModel>>(
                NoticeUiState.Loading,
            )
        val detail = mutableDetail.asStateFlow()
        private var listRequest: Job? = null
        private var detailRequest: Job? = null
        var noticeId: Long? = null
            private set

        init {
            retry()
        }

        fun selectCategory(value: BoardCategory) {
            if (category.value == value) return
            mutableCategory.value = value
            savedState[CATEGORY_KEY] = value.name
            listRequest?.cancel()
            retry()
        }

        fun retry() {
            if (listRequest?.isActive == true) return
            val type = category.value.noticeType
            listRequest =
                fetch(mutableNotices) {
                    repository.fetchNotices(type).takeIf { it.isNotEmpty() }?.map { it.toUiModel() }
                }
        }

        fun showNotice(id: Long?) {
            if (noticeId == id) return
            detailRequest?.cancel()
            noticeId = id
            mutableDetail.value = NoticeUiState.Loading
            retryDetail()
        }

        fun retryDetail() {
            val id = noticeId ?: return
            if (detailRequest?.isActive == true) return
            detailRequest = fetch(mutableDetail) { repository.fetchNotice(id)?.toUiModel() }
        }

        private fun <T> fetch(
            state: MutableStateFlow<NoticeUiState<T>>,
            request: suspend () -> T?,
        ): Job {
            state.value = NoticeUiState.Loading
            return viewModelScope.launch {
                try {
                    val value = request()
                    currentCoroutineContext().ensureActive()
                    state.value = value?.let { NoticeUiState.Content(it) } ?: NoticeUiState.Empty
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    currentCoroutineContext().ensureActive()
                    state.value = NoticeUiState.Error
                }
            }
        }

        companion object {
            const val CATEGORY_KEY = "boardCategory"
        }
    }
