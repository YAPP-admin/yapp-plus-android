package com.yapp.plus.feature.main.home.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class HomeReportTipVisibilityTest {
    private val sessionDate = LocalDate.of(2026, 11, 28)
    private val sessionStartTime = LocalTime.of(14, 0)

    @Test
    fun hidesBeforeMondayAtNineInTheSessionWeek() {
        assertFalse(isVisibleAt("2026-11-16T00:00:00Z"))
        assertFalse(isVisibleAt("2026-11-22T23:59:59Z"))
    }

    @Test
    fun showsFromMondayAtNineUntilTheSessionStarts() {
        assertTrue(isVisibleAt("2026-11-23T00:00:00Z"))
        assertTrue(isVisibleAt("2026-11-28T04:59:59Z"))
    }

    @Test
    fun hidesAtTheSessionStartAndAfterward() {
        assertFalse(isVisibleAt("2026-11-28T05:00:00Z"))
        assertFalse(isVisibleAt("2026-11-28T05:00:01Z"))
    }

    @Test
    fun hidesWhenSessionDetailsAreMissing() {
        val now = Instant.parse("2026-11-23T00:00:00Z")
        assertFalse(shouldShowReportTip(null, sessionStartTime, now))
        assertFalse(shouldShowReportTip(sessionDate, null, now))
    }

    @Test
    fun hidesWhenTheMondaySessionStartsBeforeNine() {
        val monday = LocalDate.of(2026, 11, 23)
        val earlySessionStartTime = LocalTime.of(8, 30)
        val now = Instant.parse("2026-11-22T23:00:00Z")

        assertFalse(shouldShowReportTip(monday, earlySessionStartTime, now))
    }

    private fun isVisibleAt(now: String): Boolean =
        shouldShowReportTip(sessionDate, sessionStartTime, Instant.parse(now))
}
