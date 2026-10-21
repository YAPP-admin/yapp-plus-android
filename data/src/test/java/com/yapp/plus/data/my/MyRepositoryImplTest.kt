package com.yapp.plus.data.my

import com.yapp.plus.domain.my.MyAttendanceRecord
import com.yapp.plus.domain.my.MyProfile
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class MyRepositoryImplTest {
    @Test
    fun mapsProfileAndAttendanceHistoryFromDataSource() =
        runTest {
            val profile = MyProfileData("홍길동")
            val record = attendanceRecordData
            val repository = MyRepositoryImpl(FakeMyDataSource(profile, listOf(record)))

            assertEquals(MyProfile("홍길동"), repository.fetchProfile())
            assertEquals(
                listOf(
                    MyAttendanceRecord(
                        sessionId = 42,
                        sessionName = "테스트 세션",
                        startsAt = Instant.parse("2026-10-24T05:00:00Z"),
                        endsAt = Instant.parse("2026-10-24T09:00:00Z"),
                        checkedInAt = Instant.parse("2026-10-24T04:45:00Z"),
                    ),
                ),
                repository.fetchAttendanceHistory(),
            )
        }

    @Test
    fun keepsMissingProfileAndEmptyHistory() =
        runTest {
            val repository = MyRepositoryImpl(FakeMyDataSource(profile = null, attendanceHistory = emptyList()))

            assertEquals(null, repository.fetchProfile())
            assertEquals(emptyList<MyAttendanceRecord>(), repository.fetchAttendanceHistory())
        }
}

private class FakeMyDataSource(
    private val profile: MyProfileData?,
    private val attendanceHistory: List<MyAttendanceRecordData>,
) : MyDataSource {
    override suspend fun fetchProfile(): MyProfileData? = profile

    override suspend fun fetchAttendanceHistory(): List<MyAttendanceRecordData> = attendanceHistory
}

private val attendanceRecordData =
    MyAttendanceRecordData(
        sessionId = 42,
        sessionName = "테스트 세션",
        startsAt = Instant.parse("2026-10-24T05:00:00Z"),
        endsAt = Instant.parse("2026-10-24T09:00:00Z"),
        checkedInAt = Instant.parse("2026-10-24T04:45:00Z"),
    )
