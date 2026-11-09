package com.yapp.plus.notification

import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.core.network.auth.Tokens
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PushRegistrationTest {
    private val environment = FakePushEnvironment()
    private val tokens = TokenStore(Tokens("access", "refresh", "session-a"))
    private val devices = FakeNotificationDevices()
    private val preferences = FakeNotificationPreferences()

    @Test
    fun savedOffSurvivesNewCoordinatorLoginAndTokenRotation() =
        runTest {
            preferences.enabled = false
            val first = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            first.synchronizeNow()
            tokens.updateTokens(Tokens("next-access", "next-refresh", "session-b"))
            environment.token = "token-b"
            val restarted = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            restarted.synchronizeNow()
            restarted.synchronizeNow()
            assertEquals(List(3) { "DELETE installation" }, devices.calls)
        }

    @Test
    fun mutationWaitsForServerAndQueuedSyncUsesCommittedOff() =
        runTest {
            val response = CompletableDeferred<Unit>()
            devices.unregister = { response.await() }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            val update = launch { registration.updateEnabled(false) }
            runCurrent()
            assertTrue(preferences.enabled)
            registration.synchronize()
            runCurrent()
            assertEquals(listOf("DELETE installation"), devices.calls)
            response.complete(Unit)
            update.join()
            runCurrent()
            assertFalse(preferences.enabled)
            assertEquals(List(2) { "DELETE installation" }, devices.calls)
        }

    @Test
    fun failedDeleteKeepsPreviousChoiceAndReconcilesIt() =
        runTest {
            devices.unregister = { error("offline") }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            assertTrue(runCatching { registration.updateEnabled(false) }.isFailure)
            runCurrent()
            assertTrue(preferences.enabled)
            assertEquals(listOf("DELETE installation", "PUT installation token-a"), devices.calls)
        }

    @Test
    fun failedEnableKeepsOffAndReconcilesWithDelete() =
        runTest {
            preferences.enabled = false
            devices.register = { error("offline") }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            assertTrue(runCatching { registration.updateEnabled(true) }.isFailure)
            runCurrent()
            assertFalse(preferences.enabled)
            assertEquals(listOf("PUT installation token-a", "DELETE installation"), devices.calls)
        }

    @Test
    fun localWriteFailureRestoresPreviouslySavedRegistration() =
        runTest {
            preferences.enabled = false
            preferences.write = { error("disk full") }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            assertTrue(runCatching { registration.updateEnabled(true) }.isFailure)
            runCurrent()
            assertFalse(preferences.enabled)
            assertEquals(listOf("PUT installation token-a", "DELETE installation"), devices.calls)
        }

    @Test
    fun deniedPermissionDeletesExistingRegistrationButKeepsUserChoice() =
        runTest {
            environment.permission = false
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            registration.synchronizeNow()
            assertTrue(runCatching { registration.updateEnabled(true) }.isFailure)
            assertTrue(preferences.enabled)
            assertEquals(listOf("DELETE installation"), devices.calls)
            environment.permission = true
            registration.synchronizeNow()
            assertEquals("PUT installation token-a", devices.calls.last())
        }

    @Test
    fun changedSessionWhileObtainingFidCannotSubmitOrSaveOldSelection() =
        runTest {
            val fid = CompletableDeferred<String>()
            environment.installation = { fid.await() }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            var failed = false
            val request =
                launch {
                    failed = runCatching { registration.updateEnabled(false) }.isFailure
                }
            runCurrent()
            tokens.updateTokens(null)
            fid.complete("installation")
            request.join()
            runCurrent()
            assertTrue(failed)
            assertTrue(preferences.enabled)
            assertTrue(devices.calls.isEmpty())
        }

    @Test
    fun unavailableConfigurationSessionOrStorageNeverRegisters() =
        runTest {
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            environment.configured = false
            registration.synchronizeNow()
            environment.configured = true
            tokens.updateTokens(null)
            registration.synchronizeNow()
            tokens.updateTokens(Tokens("access", "refresh"))
            preferences.read = { error("unreadable") }
            assertTrue(runCatching { registration.synchronizeNow() }.isFailure)
            assertTrue(devices.calls.isEmpty())
        }

    @Test
    fun unchangedRegistrationIsDeduplicatedButNewTokenIsSent() =
        runTest {
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            registration.synchronizeNow()
            registration.synchronizeNow()
            environment.token = "token-b"
            registration.synchronizeNow()
            assertEquals(
                listOf("PUT installation token-a", "PUT installation token-b"),
                devices.calls,
            )
        }

    @Test
    fun offWaitsForAutomaticRegistrationAndDeletesItLast() =
        runTest {
            val response = CompletableDeferred<Unit>()
            devices.register = { response.await() }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            registration.synchronize()
            runCurrent()
            val update = launch { registration.updateEnabled(false) }
            runCurrent()
            assertEquals(listOf("PUT installation token-a"), devices.calls)
            response.complete(Unit)
            update.join()
            runCurrent()
            assertFalse(preferences.enabled)
            assertEquals(listOf("PUT installation token-a", "DELETE installation"), devices.calls)
        }

    @Test
    fun logoutDuringDeleteCannotCommitThePendingChoice() =
        runTest {
            val response = CompletableDeferred<Unit>()
            devices.unregister = { response.await() }
            val registration = PushRegistration(environment, tokens, devices, preferences, backgroundScope)
            var failed = false
            val update =
                launch {
                    failed = runCatching { registration.updateEnabled(false) }.isFailure
                }
            runCurrent()
            tokens.updateTokens(null)
            response.complete(Unit)
            update.join()
            runCurrent()
            assertTrue(failed)
            assertTrue(preferences.enabled)
            assertEquals(listOf("DELETE installation"), devices.calls)
        }
}
