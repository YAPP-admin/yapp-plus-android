package com.yapp.plus.data.attendance.read

interface AttendanceReadDataSource {
    suspend fun fetchScore(): AttendanceScoreData?

    suspend fun fetchHistory(cursor: String?): AttendanceHistoryData
}
