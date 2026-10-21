package com.yapp.plus.data.my

import com.yapp.plus.domain.my.MyAttendanceRecord
import com.yapp.plus.domain.my.MyProfile
import com.yapp.plus.domain.my.MyRepository

class MyRepositoryImpl(
    private val dataSource: MyDataSource,
) : MyRepository {
    override suspend fun fetchProfile(): MyProfile? =
        dataSource.fetchProfile()?.let { MyProfile(displayName = it.displayName) }

    override suspend fun fetchAttendanceHistory(): List<MyAttendanceRecord> =
        dataSource.fetchAttendanceHistory().map { record ->
            MyAttendanceRecord(
                sessionId = record.sessionId,
                sessionName = record.sessionName,
                startsAt = record.startsAt,
                endsAt = record.endsAt,
                checkedInAt = record.checkedInAt,
            )
        }
}
