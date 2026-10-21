package com.yapp.plus.data.my

interface MyDataSource {
    suspend fun fetchProfile(): MyProfileData?

    suspend fun fetchAttendanceHistory(): List<MyAttendanceRecordData>
}
