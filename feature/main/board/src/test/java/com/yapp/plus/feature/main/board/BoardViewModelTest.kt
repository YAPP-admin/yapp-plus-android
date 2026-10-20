package com.yapp.plus.feature.main.board

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.domain.notice.Notice
import com.yapp.plus.domain.notice.NoticeRepository
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.NoticeUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setMain() = Dispatchers.setMain(dispatcher)

    @After fun resetMain() = Dispatchers.resetMain()

    @Test
    fun filtersUseOnlyConfirmedTypesAndRestoreSelection() =
        runTest(dispatcher) {
            val calls = mutableListOf<NoticeType?>()
            val repository =
                repository(list = { type ->
                    calls.add(type)
                    listOf(notice(2, NoticeType.SESSION), notice(1))
                        .filter { type == null || it.type == type }
                })
            val savedState = SavedStateHandle()
            val viewModel = BoardViewModel(repository, savedState)
            assertEquals(2, (viewModel.notices.value as NoticeUiState.Content).value.size)
            viewModel.selectCategory(BoardCategory.Operation)
            val operation = (viewModel.notices.value as NoticeUiState.Content).value.single()
            assertEquals(BoardCategory.Operation, operation.category)
            assertEquals("2026-10-20", operation.date)
            viewModel.selectCategory(BoardCategory.Session)
            viewModel.selectCategory(BoardCategory.Session)
            assertEquals(listOf(null, NoticeType.OPERATION, NoticeType.SESSION), calls)
            val restoredValues = savedState.keys().associateWith { savedState.get<Any>(it) }
            val restored = BoardViewModel(repository, SavedStateHandle(restoredValues))
            assertEquals(BoardCategory.Session, restored.category.value)
            assertEquals(NoticeType.SESSION, calls.last())
        }

    @Test
    fun failedListRetriesOnceAndCanBecomeEmpty() =
        runTest(dispatcher) {
            var calls = 0
            val response = CompletableDeferred<List<Notice>>()
            val viewModel =
                BoardViewModel(
                    repository(list = {
                        if (++calls == 1) error("sensitive server message")
                        response.await()
                    }),
                    SavedStateHandle(),
                )
            assertEquals(NoticeUiState.Error, viewModel.notices.value)
            viewModel.retry()
            viewModel.retry()
            assertEquals(2, calls)
            assertEquals(NoticeUiState.Loading, viewModel.notices.value)
            response.complete(emptyList())
            advanceUntilIdle()
            assertEquals(NoticeUiState.Empty, viewModel.notices.value)
        }

    @Test
    fun changingSelectionCancelsRequestsAndDetailHandlesMissingAndFailure() =
        runTest(dispatcher) {
            var shouldFail = false
            val viewModel =
                BoardViewModel(
                    repository(
                        list = { type ->
                            if (type == null) delay(1_000)
                            listOf(notice(if (type == null) 1 else 2, NoticeType.SESSION))
                        },
                        detail = { id ->
                            if (id == 1L) delay(1_000)
                            if (shouldFail) error("private detail")
                            if (id == 3L) null else notice(id)
                        },
                    ),
                    SavedStateHandle(),
                )
            viewModel.selectCategory(BoardCategory.Session)
            advanceUntilIdle()
            assertEquals(2L, (viewModel.notices.value as NoticeUiState.Content).value.single().id)
            viewModel.showNotice(1)
            viewModel.showNotice(2)
            advanceUntilIdle()
            assertEquals(2L, (viewModel.detail.value as NoticeUiState.Content).value.id)
            shouldFail = true
            viewModel.retryDetail()
            assertEquals(NoticeUiState.Error, viewModel.detail.value)
            shouldFail = false
            viewModel.showNotice(3)
            assertEquals(NoticeUiState.Empty, viewModel.detail.value)
            viewModel.showNotice(null)
            assertEquals(NoticeUiState.Loading, viewModel.detail.value)
        }

    private fun notice(
        id: Long,
        type: NoticeType = NoticeType.OPERATION,
    ) = Notice(id, "공지", "본문", type, Instant.parse("2026-10-19T15:00:00Z"))

    private fun repository(
        list: suspend (NoticeType?) -> List<Notice> = { emptyList() },
        detail: suspend (Long) -> Notice? = { null },
    ) = object : NoticeRepository {
        override suspend fun fetchNotices(type: NoticeType?) = list(type)

        override suspend fun fetchNotice(noticeId: Long) = detail(noticeId)
    }
}
