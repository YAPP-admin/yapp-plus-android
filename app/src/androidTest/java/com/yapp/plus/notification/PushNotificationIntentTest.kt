package com.yapp.plus.notification

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PushNotificationIntentTest {
    @Test
    fun opensPostOnlyForPostPayloadWithPositiveId() {
        val intent = Intent().putExtra(PUSH_SOURCE, PUSH_POST).putExtra(PUSH_POST_ID, "42")

        assertEquals(42L, intent.pushPostId())
        assertNull(Intent().putExtra(PUSH_POST_ID, "42").pushPostId())
        assertNull(intent.putExtra(PUSH_POST_ID, "0").pushPostId())
        assertNull(intent.putExtra(PUSH_POST_ID, "9223372036854775808").pushPostId())
        assertNull(intent.putExtra(PUSH_POST_ID, 42L).pushPostId())
    }
}
