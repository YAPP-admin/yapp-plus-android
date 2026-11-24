package com.yapp.plus.data.attendance.team

import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendancePhotoImage
import com.yapp.plus.domain.attendance.TeamAttendancePhotoStatus
import java.net.URI
import java.time.Instant
import java.time.LocalTime

data class TeamPhotoDetailsData(
    val photoId: Long,
    val teamId: Long,
    val teamName: String,
    val status: String,
    val imageUrl: String?,
    val imageExpiresAt: Instant?,
    val uploadedAt: Instant,
    val uploadedTime: LocalTime,
    val submittedAt: Instant,
    val uploaderName: String?,
    val selectedMemberCount: Int,
) {
    fun toDomain(): TeamAttendancePhoto {
        require(photoId > 0 && teamId > 0 && selectedMemberCount >= 0)
        return TeamAttendancePhoto(
            photoId = photoId,
            teamId = teamId,
            teamName = teamName,
            status = TeamAttendancePhotoStatus.valueOf(status),
            image = toImage(),
            uploadedAt = uploadedAt,
            uploadedTime = uploadedTime,
            submittedAt = submittedAt,
            uploaderName = uploaderName,
            selectedMemberCount = selectedMemberCount,
        )
    }

    private fun toImage(): TeamAttendancePhotoImage? {
        val url = imageUrl ?: return null
        val expiresAt = imageExpiresAt ?: return null
        val uri = runCatching { URI.create(url) }.getOrNull() ?: return null
        if (uri.scheme != "https" || uri.host.isNullOrBlank() || uri.userInfo != null) return null
        return TeamAttendancePhotoImage(url, expiresAt)
    }
}
