package com.yapp.plus.core.ui

import com.yapp.plus.domain.result.YappResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun eventsWaitForACollectorAndPreserveRepeatedValuesInOrder() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val viewModel =
                object : BaseViewModel<Unit, String>(Unit) {
                    suspend fun publish(event: String) = sendEvent(event)
                }

            viewModel.publish("first")
            viewModel.publish("first")
            viewModel.publish("second")

            assertEquals(listOf("first", "first", "second"), viewModel.events.take(3).toList())
        }

    @Test
    fun restartingCollectionReceivesPendingEventsWithoutReplayingConsumedEvents() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val viewModel =
                object : BaseViewModel<Unit, String>(Unit) {
                    suspend fun publish(event: String) = sendEvent(event)
                }
            val received = mutableListOf<String>()
            val collector = backgroundScope.launch { viewModel.events.toList(received) }
            viewModel.publish("consumed")
            runCurrent()
            assertEquals(listOf("consumed"), received)
            collector.cancelAndJoin()

            viewModel.publish("pending")
            backgroundScope.launch { viewModel.events.toList(received) }
            runCurrent()

            assertEquals(listOf("consumed", "pending"), received)
        }

    @Test
    fun cancelledRequestCannotReplaceANewerResultWithLateSuccessOrFailure() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val viewModel =
                object : BaseViewModel<String, Nothing>("initial") {
                    fun load(request: suspend () -> YappResult<String, String>) =
                        launchRequest(
                            request = request,
                            onStart = { "loading" },
                            onSuccess = { _, value -> value },
                            onFailure = { _, reason -> reason },
                        )
                }
            for (lateResult in listOf(YappResult.Success("stale"), YappResult.Failure("failed"))) {
                val gate = CompletableDeferred<Unit>()
                val oldJob =
                    viewModel.load {
                        withContext(NonCancellable) { gate.await() }
                        lateResult
                    }
                runCurrent()
                oldJob.cancel()
                viewModel.load { YappResult.Success("latest") }
                runCurrent()
                gate.complete(Unit)
                oldJob.join()

                assertEquals("latest", viewModel.uiState.value)
            }
        }
}
