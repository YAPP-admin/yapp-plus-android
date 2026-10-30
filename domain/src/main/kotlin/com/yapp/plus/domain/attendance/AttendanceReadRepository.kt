package com.yapp.plus.domain.attendance

interface AttendanceReadRepository {
    suspend fun fetchScore(): AttendanceScore?

    suspend fun fetchHistory(cursor: String? = null): AttendanceHistoryPage
}
