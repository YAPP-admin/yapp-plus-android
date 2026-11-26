package com.yapp.plus.domain.attendance

interface AttendanceReportQueryRepository {
    suspend fun getCurrentWeekReport(): SubmittedAttendanceReport?
}
