package com.yapp.plus.data.attendance.read

import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.AttendanceScore

class AttendanceReadRepositoryImpl(
    private val dataSource: AttendanceReadDataSource,
) : AttendanceReadRepository {
    override suspend fun fetchScore(): AttendanceScore? = dataSource.fetchScore()?.toDomain()

    override suspend fun fetchHistory(cursor: String?): AttendanceHistoryPage =
        dataSource.fetchHistory(cursor).toDomain()
}
