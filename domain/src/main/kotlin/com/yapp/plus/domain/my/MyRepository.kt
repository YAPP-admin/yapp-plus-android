package com.yapp.plus.domain.my

interface MyRepository {
    suspend fun fetchProfile(): MyProfile?

    suspend fun fetchAttendanceHistory(): List<MyAttendanceRecord>
}
