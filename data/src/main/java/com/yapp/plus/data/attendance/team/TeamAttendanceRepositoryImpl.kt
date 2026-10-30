package com.yapp.plus.data.attendance.team

import com.yapp.plus.domain.attendance.TeamAttendanceRepository
import com.yapp.plus.domain.attendance.TeamPhotoSubmission
import java.time.Instant

class TeamAttendanceRepositoryImpl(
    private val source: TeamPhotoDataSource,
) : TeamAttendanceRepository {
    override suspend fun uploadPhoto(
        sessionId: Long,
        filename: String,
        contentType: String,
        bytes: ByteArray,
    ): String {
        require(sessionId > 0 && filename.isNotBlank())
        require(contentType in setOf("image/jpeg", "image/png") && bytes.size in 1..MAX_PHOTO_BYTES)
        return source.uploadPhoto(sessionId, filename, contentType, bytes.copyOf())
    }

    override suspend fun submitPhoto(
        sessionId: Long,
        imageKey: String,
        membershipIds: Set<Long>,
    ): TeamPhotoSubmission {
        require(
            sessionId > 0 &&
                imageKey.isNotBlank() &&
                membershipIds.isNotEmpty() &&
                membershipIds.all { it > 0 },
        )
        val result = source.submitPhoto(sessionId, imageKey, membershipIds)
        check(result.photoId > 0 && result.status == "PENDING")
        return TeamPhotoSubmission(result.photoId, Instant.parse(result.submittedAt))
    }

    private companion object {
        const val MAX_PHOTO_BYTES = 10 * 1024 * 1024
    }
}
