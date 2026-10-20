package com.yapp.plus.core.network.auth

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class TokenStorePersistenceTest {
    private val original = Tokens(accessToken = "saved-access", refreshToken = "saved-refresh")
    private val rotated = Tokens(accessToken = "rotated-access", refreshToken = "rotated-refresh")

    @Test
    fun `concurrent first readers wait for one restoration`() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            val persistence = FakeTokenPersistence(original).apply { readGate = gate }
            val store = TokenStore(persistence = persistence)
            val readers = List(20) { async { store.currentTokens() } }
            persistence.readStarted.await()
            assertTrue(readers.none { it.isCompleted })
            gate.complete(Unit)
            assertTrue(readers.awaitAll().all { it?.accessToken == original.accessToken })
            assertEquals(1, persistence.readCount)
        }

    @Test
    fun `new store restores the latest complete token pair`() =
        runTest {
            val persistence = FakeTokenPersistence(original)
            TokenStore(persistence = persistence).updateTokens(rotated)
            val restored = TokenStore(persistence = persistence).currentTokens()
            assertEquals(rotated.accessToken, restored?.accessToken)
            assertEquals(rotated.refreshToken, restored?.refreshToken)
            assertEquals(1, persistence.readCount)
        }

    @Test
    fun `failed write does not publish an unpersisted token pair`() =
        runTest {
            val persistence = FakeTokenPersistence(original)
            val store = TokenStore(persistence = persistence)
            val restored = store.currentTokens()
            val failure = IOException("disk unavailable")
            persistence.writeFailure = failure
            val actualFailure = runCatching { store.updateTokens(rotated) }.exceptionOrNull()
            assertTrue(actualFailure is IOException)
            assertEquals(failure.message, actualFailure?.message)
            assertSame(restored, store.currentTokens())
            assertEquals(original.accessToken, persistence.storedTokens?.accessToken)
        }

    @Test
    fun `caller cancellation cannot split disk and memory during commit`() =
        runTest {
            val persistence =
                FakeTokenPersistence(original).apply {
                    writeGate = CompletableDeferred()
                }
            val store = TokenStore(persistence = persistence)
            store.currentTokens()
            val write = launch { store.updateTokens(rotated) }
            persistence.writeStarted.await()
            write.cancel()
            persistence.writeGate?.complete(Unit)
            write.join()
            assertEquals(rotated.accessToken, persistence.storedTokens?.accessToken)
            assertEquals(rotated.refreshToken, store.currentTokens()?.refreshToken)
        }

    @Test
    fun `cleared session stays empty after reconstructing the store`() =
        runTest {
            val persistence = FakeTokenPersistence(original)
            TokenStore(persistence = persistence).updateTokens(null)
            assertNull(TokenStore(persistence = persistence).currentTokens())
            assertNull(persistence.storedTokens)
        }

    @Test
    fun `failed restoration is retried instead of caching an empty session`() =
        runTest {
            val failure = IOException("temporarily unavailable")
            val persistence = FakeTokenPersistence(original).apply { readFailure = failure }
            val store = TokenStore(persistence = persistence)
            assertSame(failure, runCatching { store.currentTokens() }.exceptionOrNull())
            persistence.readFailure = null
            assertEquals(original.refreshToken, store.currentTokens()?.refreshToken)
            assertEquals(2, persistence.readCount)
        }

    @Test
    fun `stale renewal cannot overwrite a replacement session on disk`() =
        runTest {
            val persistence = FakeTokenPersistence(original)
            val store = TokenStore(persistence = persistence)
            val expired = checkNotNull(store.currentTokens())
            val replacement = Tokens(accessToken = "other-access", refreshToken = "other-refresh")
            store.updateTokens(replacement)
            assertFalse(store.replaceTokens(expected = expired, updated = rotated))
            assertSame(replacement, store.currentTokens())
            assertSame(replacement, persistence.storedTokens)
        }
}
