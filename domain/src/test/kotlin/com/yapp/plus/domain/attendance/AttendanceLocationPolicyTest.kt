package com.yapp.plus.domain.attendance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AttendanceLocationPolicyTest {
    private val boundary = AttendanceLocationReading.Measured(meters = 100f, accuracyMeters = 50f)

    @Test
    fun allowsExactBoundariesAndDoesNotRoundDistance() {
        assertNull(AttendanceLocationPolicy.blockingReason(boundary))
        assertEquals(
            AttendanceLocationFailure.OUT_OF_RANGE,
            AttendanceLocationPolicy.blockingReason(boundary.copy(meters = 100.01f)),
        )
    }

    @Test
    fun rejectsMissingOrInsufficientAccuracy() {
        for (accuracy in listOf(null, -1f, 50.01f, Float.NaN, Float.POSITIVE_INFINITY)) {
            assertEquals(
                AttendanceLocationFailure.INSUFFICIENT_ACCURACY,
                AttendanceLocationPolicy.blockingReason(boundary.copy(accuracyMeters = accuracy)),
            )
        }
    }

    @Test
    fun missingInvalidOrInterruptedMeasurementsCannotAllowSubmission() {
        val failures =
            listOf(
                AttendanceLocationFailure.PRECISE_PERMISSION_REQUIRED,
                AttendanceLocationFailure.LOCATION_DISABLED,
                AttendanceLocationFailure.TIMED_OUT,
                AttendanceLocationFailure.UNAVAILABLE,
            )
        for (reason in failures) {
            val reading = AttendanceLocationReading.Failed(reason)
            assertEquals(reason, AttendanceLocationPolicy.blockingReason(reading))
        }
        for (meters in listOf(-1f, Float.NaN, Float.POSITIVE_INFINITY)) {
            assertEquals(
                AttendanceLocationFailure.UNAVAILABLE,
                AttendanceLocationPolicy.blockingReason(boundary.copy(meters = meters)),
            )
        }
    }
}
