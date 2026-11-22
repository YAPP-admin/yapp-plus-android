package com.yapp.plus.domain.attendance

object AttendanceLocationPolicy {
    fun blockingReason(reading: AttendanceLocationReading): AttendanceLocationFailure? =
        when (reading) {
            is AttendanceLocationReading.Failed -> reading.reason
            is AttendanceLocationReading.Measured -> checkMeasurement(reading)
        }

    private fun checkMeasurement(reading: AttendanceLocationReading.Measured): AttendanceLocationFailure? {
        val accuracy = reading.accuracyMeters
        return when {
            accuracy == null || accuracy !in 0f..MAX_ACCURACY_METERS ->
                AttendanceLocationFailure.INSUFFICIENT_ACCURACY
            !reading.meters.isFinite() || reading.meters < 0f ->
                AttendanceLocationFailure.UNAVAILABLE
            reading.meters > MAX_DISTANCE_METERS -> AttendanceLocationFailure.OUT_OF_RANGE
            else -> null
        }
    }

    private const val MAX_ACCURACY_METERS = 50f
    private const val MAX_DISTANCE_METERS = 100f
}
