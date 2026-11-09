package com.yapp.plus.notification

import androidx.lifecycle.viewModelScope
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.core.network.auth.Tokens
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val preferences = FakeNotificationPreferences()
    private val devices = FakeNotificationDevices()
    private val environment = FakePushEnvironment()
    private val viewModels = mutableListOf<NotificationSettingsViewModel>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        viewModels.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
    }

    @Test
    fun failedLoadCanRetryAndReflectPermissionChange() =
        runTest(dispatcher) {
            preferences.read = { error("unreadable") }
            environment.permission = false
            val viewModel = createViewModel()
            viewModel.refresh()
            runCurrent()
            assertNull(viewModel.state.value.isEnabled)
            assertTrue(viewModel.state.value.hasLoadError)
            assertFalse(viewModel.state.value.isPermissionGranted)
            val response = CompletableDeferred<Boolean>()
            var reads = 0
            preferences.read = {
                reads++
                response.await()
            }
            environment.permission = true
            viewModel.retry()
            viewModel.retry()
            runCurrent()
            assertTrue(viewModel.state.value.hasLoadError)
            assertTrue(viewModel.state.value.isLoading)
            assertEquals(1, reads)
            response.complete(false)
            runCurrent()
            assertEquals(false, viewModel.state.value.isEnabled)
            assertFalse(viewModel.state.value.hasLoadError)
            assertFalse(viewModel.state.value.isLoading)
            assertTrue(viewModel.state.value.isPermissionGranted)
        }

    @Test
    fun pendingChangeBlocksDuplicatesAndFailedChangeRetriesSameSelection() =
        runTest(dispatcher) {
            val response = CompletableDeferred<Unit>()
            devices.unregister = { response.await() }
            val viewModel = createViewModel()
            viewModel.refresh()
            runCurrent()
            viewModel.updateEnabled(false)
            viewModel.updateEnabled(false)
            runCurrent()
            assertTrue(viewModel.state.value.isUpdating)
            assertEquals(true, viewModel.state.value.isEnabled)
            assertEquals(1, devices.calls.size)
            response.completeExceptionally(IllegalStateException("offline"))
            runCurrent()
            assertFalse(viewModel.state.value.isUpdating)
            assertTrue(viewModel.state.value.hasUpdateError)
            assertEquals(true, viewModel.state.value.isEnabled)
            devices.unregister = {}
            viewModel.retry()
            runCurrent()
            assertFalse(viewModel.state.value.hasUpdateError)
            assertEquals(false, viewModel.state.value.isEnabled)
            assertFalse(preferences.enabled)
        }

    @Test
    fun permissionResultDuringRefreshStillEnablesNotifications() =
        runTest(dispatcher) {
            preferences.enabled = false
            val viewModel = createViewModel()
            viewModel.refresh()
            runCurrent()
            val pendingRead = CompletableDeferred<Boolean>()
            preferences.read = { pendingRead.await() }
            viewModel.refresh()
            runCurrent()
            viewModel.updateEnabled(true)
            runCurrent()
            assertTrue(preferences.enabled)
            assertEquals(true, viewModel.state.value.isEnabled)
            assertFalse(viewModel.state.value.isUpdating)
        }

    @Test
    fun cancellationPreservesChoiceAndClearsProgress() =
        runTest(dispatcher) {
            devices.unregister = { throw CancellationException("cancelled") }
            val viewModel = createViewModel()
            viewModel.refresh()
            runCurrent()
            viewModel.updateEnabled(false)
            runCurrent()
            assertTrue(preferences.enabled)
            assertEquals(true, viewModel.state.value.isEnabled)
            assertFalse(viewModel.state.value.isUpdating)
            assertFalse(viewModel.state.value.hasUpdateError)
        }

    @Test
    fun refreshFailureAfterLoadedValueIsStillAQueryFailure() =
        runTest(dispatcher) {
            val viewModel = createViewModel()
            viewModel.refresh()
            runCurrent()
            preferences.read = { error("unreadable") }
            viewModel.refresh()
            runCurrent()
            assertEquals(true, viewModel.state.value.isEnabled)
            assertTrue(viewModel.state.value.hasLoadError)
            assertFalse(viewModel.state.value.hasUpdateError)
            assertFalse(viewModel.state.value.isLoading)
        }

    private fun TestScope.createViewModel(): NotificationSettingsViewModel {
        val registration =
            PushRegistration(
                environment,
                TokenStore(Tokens("access", "refresh")),
                devices,
                preferences,
                backgroundScope,
            )
        return NotificationSettingsViewModel(registration).also { viewModels += it }
    }
}
