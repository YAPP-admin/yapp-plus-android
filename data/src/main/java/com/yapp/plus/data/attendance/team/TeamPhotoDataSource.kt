package com.yapp.plus.data.attendance.team

interface TeamPhotoDataSource {
    suspend fun uploadPhoto(
        sessionId: Long,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): String

    suspend fun submitPhoto(
        sessionId: Long,
        imageKey: String,
        membershipIds: Set<Long>,
    ): TeamPhotoSubmissionData
}
