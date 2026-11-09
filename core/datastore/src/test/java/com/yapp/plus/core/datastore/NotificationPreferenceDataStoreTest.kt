package com.yapp.plus.core.datastore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class NotificationPreferenceDataStoreTest {
    @get:Rule
    val directory = TemporaryFolder()

    @Test
    fun defaultOnAndSavedOffSurviveStoreRecreation() =
        runTest {
            val file = File(directory.root, "preference")
            val firstScope = CoroutineScope(coroutineContext + SupervisorJob())
            val first = NotificationPreferenceDataStore(file, firstScope)
            assertTrue(first.fetchEnabled())
            first.updateEnabled(false)
            firstScope.coroutineContext[Job]!!.cancelAndJoin()

            val secondScope = CoroutineScope(coroutineContext + SupervisorJob())
            try {
                val restored = NotificationPreferenceDataStore(file, secondScope)
                assertFalse(restored.fetchEnabled())
                restored.updateEnabled(true)
                assertTrue(restored.fetchEnabled())
            } finally {
                secondScope.coroutineContext[Job]!!.cancelAndJoin()
            }
        }

    @Test
    fun corruptedChoiceDoesNotSilentlyEnableNotifications() =
        runTest {
            val file = directory.newFile("preference").apply { writeText("broken") }
            val scope = CoroutineScope(coroutineContext + SupervisorJob())
            try {
                val store = NotificationPreferenceDataStore(file, scope)
                assertFalse(store.fetchEnabled())
                store.updateEnabled(true)
                assertTrue(store.fetchEnabled())
            } finally {
                scope.coroutineContext[Job]!!.cancelAndJoin()
            }
        }
}
