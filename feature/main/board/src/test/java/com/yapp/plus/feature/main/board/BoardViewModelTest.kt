package com.yapp.plus.feature.main.board

import androidx.lifecycle.SavedStateHandle
import com.yapp.plus.domain.notice.NoticeType
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.NoticeUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun categoryRestoresAndCancelsPreviousRequest() =
        runTest {
            val repository = FakeNoticeRepository()
            val requests = mutableListOf<NoticeType?>()
            val delayed = CompletableDeferred<Unit>()
            repository.fetchPage = { type, _ ->
                requests += type
                if (type == null) delayed.await()
                repository.page(if (type == null) 1 else 2)
            }
            val saved = SavedStateHandle()
            val viewModel = BoardViewModel(saved, repository)
            viewModel.selectCategory(BoardCategory.Assignment)
            delayed.complete(Unit)
            advanceUntilIdle()
            assertEquals(listOf(null, NoticeType.ASSIGNMENT), requests)
            assertEquals(
                2L,
                (viewModel.uiState.value.notices as NoticeUiState.Content).value.single().id,
            )
            val restored =
                BoardViewModel(
                    SavedStateHandle(saved.keys().associateWith { saved.get<Any>(it) }),
                    repository,
                )
            assertEquals(BoardCategory.Assignment, restored.uiState.value.category)
        }

    @Test fun paginationDeduplicatesAndRetainsDataOnFailureThenRetries() =
        runTest {
            val repository = FakeNoticeRepository()
            var shouldFail = true
            repository.fetchPage = { _, cursor ->
                if (cursor == null) {
                    repository.page(1, cursor = "page2")
                } else {
                    if (shouldFail) error("offline")
                    repository.page(1, 2)
                }
            }
            val viewModel = BoardViewModel(SavedStateHandle(), repository)
            viewModel.fetchNextPage()
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.hasLoadMoreError)
            assertEquals(1, (viewModel.uiState.value.notices as NoticeUiState.Content).value.size)
            shouldFail = false
            viewModel.fetchNextPage()
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.hasLoadMoreError)
            assertEquals(
                listOf(
                    1L,
                    2L,
                ),
                (viewModel.uiState.value.notices as NoticeUiState.Content).value.map {
                    it.id
                },
            )
        }

    @Test fun openingDetailDoesNotMarkReadAndConfirmationWaitsForServer() =
        runTest {
            val repository = FakeNoticeRepository()
            val confirmed = CompletableDeferred<Unit>()
            repository.read = { confirmed.await() }
            repository.fetchDetail =
                {
                    repository.detail(
                        it,
                        confirmed.isCompleted,
                        if (confirmed.isCompleted) 8 else 7,
                    )
                }
            val viewModel = BoardViewModel(SavedStateHandle(), repository)
            viewModel.showNotice(4)
            assertEquals(0, repository.readRequests)
            viewModel.markRead()
            viewModel.markRead()
            assertEquals(1, repository.readRequests)
            assertFalse((viewModel.uiState.value.detail as NoticeUiState.Content).value.isRead)
            confirmed.complete(Unit)
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value.detail as NoticeUiState.Content).value.isRead)
            assertEquals(
                8L,
                (viewModel.uiState.value.detail as NoticeUiState.Content).value.readCount,
            )
            assertFalse(viewModel.uiState.value.isMarkingRead)
        }

    @Test fun detailAndReadFailuresCanRetryWithoutClaimingSuccess() =
        runTest {
            val repository = FakeNoticeRepository()
            repository.fetchDetail = { error("offline") }
            val viewModel = BoardViewModel(SavedStateHandle(), repository)
            viewModel.showNotice(4)
            assertEquals(NoticeUiState.Error, viewModel.uiState.value.detail)
            repository.fetchDetail = { repository.detail(it) }
            viewModel.retry()
            repository.read = { error("offline") }
            viewModel.markRead()
            assertTrue(viewModel.uiState.value.hasReadError)
            assertFalse((viewModel.uiState.value.detail as NoticeUiState.Content).value.isRead)
            repository.read = {}
            repository.fetchDetail = { repository.detail(it, isRead = true, count = 1) }
            viewModel.markRead()
            assertFalse(viewModel.uiState.value.hasReadError)
            assertTrue((viewModel.uiState.value.detail as NoticeUiState.Content).value.isRead)
        }
}
