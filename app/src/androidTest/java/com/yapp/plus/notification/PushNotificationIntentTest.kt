package com.yapp.plus.notification

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yapp.plus.domain.notification.SettlementNotificationTarget
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

    @Test
    fun opensScheduleOnlyForSessionRemindersWithPositiveIdAndMatchingTarget() {
        val intent =
            Intent()
                .putExtra(PUSH_SOURCE, PUSH_SESSION_REMINDER)
                .putExtra(PUSH_SESSION_ID, "42")
        assertNull(intent.pushSessionId())
        intent.putExtra(PUSH_NAVIGATION_TARGET, PUSH_SESSION_SCHEDULE)
        assertEquals(42L, intent.pushSessionId())
        assertNull(intent.putExtra(PUSH_SESSION_ID, "0").pushSessionId())
        assertNull(intent.putExtra(PUSH_SESSION_ID, "9223372036854775808").pushSessionId())
    }

    @Test
    fun opensSettlementOnlyWithMatchingTargetAndPositiveIds() {
        val intent =
            Intent()
                .putExtra(PUSH_SOURCE, PUSH_SETTLEMENT)
                .putExtra(PUSH_AFTERPARTY_ID, "9")
                .putExtra(PUSH_SETTLEMENT_ID, "13")
        assertNull(intent.pushSettlement())

        intent.putExtra(PUSH_NAVIGATION_TARGET, PUSH_SESSION_SCHEDULE)
        assertNull(intent.pushSettlement())

        intent.putExtra(PUSH_NAVIGATION_TARGET, PUSH_SETTLEMENT)
        assertEquals(SettlementNotificationTarget(9, 13), intent.pushSettlement())
        assertNull(intent.putExtra(PUSH_AFTERPARTY_ID, "0").pushSettlement())
        assertNull(intent.putExtra(PUSH_AFTERPARTY_ID, "9").putExtra(PUSH_SETTLEMENT_ID, "-1").pushSettlement())
    }
}
