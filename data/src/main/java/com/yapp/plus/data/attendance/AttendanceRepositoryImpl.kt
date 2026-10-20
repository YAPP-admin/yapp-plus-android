package com.yapp.plus.data.attendance

import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.domain.attendance.AttendanceRepository
import io.ktor.client.HttpClient

class AttendanceRepositoryImpl(
    httpClient: HttpClient,
) : AttendanceRepository {
    private val remoteDataSource = AttendanceRemoteDataSource(httpClient)

    override suspend fun verifyAttendance(
        sessionId: Long,
        code: Int,
    ) = remoteDataSource.verifyAttendance(sessionId, code)

    override suspend fun submitReport(
        type: AttendanceReportType,
        reason: String,
    ) = remoteDataSource.submitReport(type.name, reason)
}
