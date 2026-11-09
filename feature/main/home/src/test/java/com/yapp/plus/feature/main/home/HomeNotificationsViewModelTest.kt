package com.yapp.plus.feature.main.home

import com.yapp.plus.domain.notification.Notification
import com.yapp.plus.domain.notification.NotificationPage
import com.yapp.plus.domain.notification.NotificationRepository
import com.yapp.plus.feature.main.home.model.HomeNotificationsUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class HomeNotificationsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun retriesFirstPageAndPreservesListUntilNextPageSucceeds() =
        runTest {
            val repository = FakeNotifications()
            val viewModel = HomeNotificationsViewModel(repository)
            assertEquals(HomeNotificationsUiState.Error, viewModel.uiState.value)

            repository.pages[null] = NotificationPage(listOf(notification(3)), "next")
            viewModel.refresh()
            viewModel.loadMore()
            val failed = viewModel.uiState.value as HomeNotificationsUiState.Content
            assertEquals(listOf(3L), failed.items.map { it.id })
            assertTrue(failed.hasLoadMoreError)
            assertEquals("next", failed.nextCursor)

            repository.pages["next"] = NotificationPage(listOf(notification(3), notification(2)), null)
            viewModel.loadMore()
            val loaded = viewModel.uiState.value as HomeNotificationsUiState.Content
            assertEquals(listOf(3L, 2L), loaded.items.map { it.id })
            assertFalse(loaded.hasLoadMoreError)
        }

    @Test
    fun refreshRequestsFirstPageAndKeepsOldContentWhenOffline() =
        runTest {
            val repository = FakeNotifications()
            repository.pages[null] = NotificationPage(listOf(notification(3)), null)
            val viewModel = HomeNotificationsViewModel(repository)

            repository.pages.clear()
            viewModel.refresh()
            val failed = viewModel.uiState.value as HomeNotificationsUiState.Content
            assertTrue(failed.hasRefreshError)
            assertEquals(listOf(3L), failed.items.map { it.id })

            repository.pages[null] = NotificationPage(listOf(notification(4)), null)
            viewModel.refresh()
            val refreshed = viewModel.uiState.value as HomeNotificationsUiState.Content
            assertEquals(listOf(4L), refreshed.items.map { it.id })
            assertFalse(refreshed.hasRefreshError)
        }
}

internal class FakeNotifications : NotificationRepository {
    val pages = mutableMapOf<String?, NotificationPage>()

    override suspend fun fetchNotifications(cursor: String?): NotificationPage = pages[cursor] ?: error("offline")
}

private fun notification(id: Long): Notification = Notification(id, "알림", LocalDate.of(2026, 11, 2), postId = null)
