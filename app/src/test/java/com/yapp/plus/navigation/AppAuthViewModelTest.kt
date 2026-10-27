package com.yapp.plus.navigation

import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.auth.AuthOperationResult
import com.yapp.plus.domain.auth.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppAuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val viewModels = mutableListOf<AppAuthViewModel>()

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
    fun noStoredSessionBlocksBothOperations() =
        runTest(dispatcher) {
            val repository = FakeAppAuthRepository(storedSessionExists = false)
            val viewModel = createViewModel(repository)
            runCurrent()
            viewModel.logout()
            viewModel.withdraw()
            runCurrent()
            assertFalse(viewModel.uiState.value.hasSession)
            assertEquals(0, repository.logoutRequestCount)
            assertEquals(0, repository.withdrawalRequestCount)
        }

    @Test
    fun logoutWaitsForSuccessAndBlocksDuplicateOrCompetingRequests() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<AuthOperationResult>()
            val repository = FakeAppAuthRepository().apply { logoutOperation = { pending.await() } }
            val viewModel = createViewModel(repository)
            runCurrent()

            viewModel.logout()
            assertTrue(viewModel.uiState.value.isLoggingOut)
            assertTrue(viewModel.uiState.value.hasSession)
            viewModel.logout()
            viewModel.withdraw()
            runCurrent()
            assertEquals(1, repository.logoutRequestCount)
            assertEquals(0, repository.withdrawalRequestCount)

            pending.complete(AuthOperationResult.Success)
            runCurrent()
            assertFalse(viewModel.uiState.value.hasSession)
            assertFalse(viewModel.uiState.value.isLoggingOut)
            assertFalse(viewModel.uiState.value.hasLogoutError)
        }

    @Test
    fun withdrawalWaitsForSuccessAndBlocksDuplicateOrCompetingRequests() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<AuthOperationResult>()
            val repository =
                FakeAppAuthRepository().apply { withdrawalOperation = { pending.await() } }
            val viewModel = createViewModel(repository)
            runCurrent()

            viewModel.withdraw()
            assertTrue(viewModel.uiState.value.isWithdrawing)
            assertTrue(viewModel.uiState.value.hasSession)
            viewModel.withdraw()
            viewModel.logout()
            runCurrent()
            assertEquals(1, repository.withdrawalRequestCount)
            assertEquals(0, repository.logoutRequestCount)

            pending.complete(AuthOperationResult.Success)
            runCurrent()
            assertFalse(viewModel.uiState.value.hasSession)
            assertFalse(viewModel.uiState.value.isWithdrawing)
            assertFalse(viewModel.uiState.value.hasWithdrawalError)
        }

    @Test
    fun failedLogoutPreservesSessionAndCanBeDismissedAndRetried() =
        runTest(dispatcher) {
            val repository =
                FakeAppAuthRepository().apply { logoutOperation = { AuthOperationResult.Failure } }
            val viewModel = createViewModel(repository)
            runCurrent()
            viewModel.logout()
            runCurrent()
            assertSignedInAndIdle(viewModel)
            assertTrue(viewModel.uiState.value.hasLogoutError)
            assertFalse(viewModel.uiState.value.hasWithdrawalError)

            viewModel.onLogoutErrorDismissed()
            assertFalse(viewModel.uiState.value.hasLogoutError)
            repository.logoutOperation = { AuthOperationResult.Success }
            viewModel.logout()
            runCurrent()
            assertEquals(2, repository.logoutRequestCount)
            assertFalse(viewModel.uiState.value.hasSession)
        }

    @Test
    fun failedWithdrawalPreservesSessionAndCanBeDismissedAndRetried() =
        runTest(dispatcher) {
            val repository =
                FakeAppAuthRepository().apply {
                    withdrawalOperation = { AuthOperationResult.Failure }
                }
            val viewModel = createViewModel(repository)
            runCurrent()
            viewModel.withdraw()
            runCurrent()
            assertSignedInAndIdle(viewModel)
            assertTrue(viewModel.uiState.value.hasWithdrawalError)
            assertFalse(viewModel.uiState.value.hasLogoutError)

            viewModel.onWithdrawalErrorDismissed()
            assertFalse(viewModel.uiState.value.hasWithdrawalError)
            repository.withdrawalOperation = { AuthOperationResult.Success }
            viewModel.withdraw()
            runCurrent()
            assertEquals(2, repository.withdrawalRequestCount)
            assertFalse(viewModel.uiState.value.hasSession)
        }

    @Test
    fun unsupportedOperationsProduceTheirOwnErrorsAndPreserveSession() =
        runTest(dispatcher) {
            val repository =
                FakeAppAuthRepository().apply {
                    logoutOperation = { AuthOperationResult.NotSupported }
                    withdrawalOperation = { AuthOperationResult.NotSupported }
                }
            val viewModel = createViewModel(repository)
            runCurrent()
            viewModel.logout()
            runCurrent()
            assertTrue(viewModel.uiState.value.hasLogoutError)
            viewModel.withdraw()
            runCurrent()
            assertSignedInAndIdle(viewModel)
            assertFalse(viewModel.uiState.value.hasLogoutError)
            assertTrue(viewModel.uiState.value.hasWithdrawalError)
        }

    @Test
    fun unexpectedExceptionFailsOnlyTheRequestedOperation() =
        runTest(dispatcher) {
            val repository =
                FakeAppAuthRepository().apply { logoutOperation = { error("offline") } }
            val viewModel = createViewModel(repository)
            runCurrent()
            viewModel.logout()
            runCurrent()
            assertSignedInAndIdle(viewModel)
            assertTrue(viewModel.uiState.value.hasLogoutError)
            assertFalse(viewModel.uiState.value.hasWithdrawalError)
        }

    @Test
    fun cancellationClearsProgressWithoutErrorAndAllowsRetry() =
        runTest(dispatcher) {
            for (isLogout in listOf(true, false)) {
                val repository =
                    FakeAppAuthRepository().apply {
                        logoutOperation = { throw CancellationException("cancelled") }
                        withdrawalOperation = { throw CancellationException("cancelled") }
                    }
                val viewModel = createViewModel(repository)
                runCurrent()
                val action = if (isLogout) viewModel::logout else viewModel::withdraw
                action()
                runCurrent()
                assertSignedInAndIdle(viewModel)
                assertFalse(viewModel.uiState.value.hasLogoutError)
                assertFalse(viewModel.uiState.value.hasWithdrawalError)

                repository.logoutOperation = { AuthOperationResult.Success }
                repository.withdrawalOperation = { AuthOperationResult.Success }
                action()
                runCurrent()
                assertFalse(viewModel.uiState.value.hasSession)
            }
        }

    private fun createViewModel(repository: AuthRepository): AppAuthViewModel =
        AppAuthViewModel(repository).also { viewModels += it }

    private fun assertSignedInAndIdle(viewModel: AppAuthViewModel) {
        assertTrue(viewModel.uiState.value.hasSession)
        assertFalse(viewModel.uiState.value.isLoggingOut)
        assertFalse(viewModel.uiState.value.isWithdrawing)
    }
}
