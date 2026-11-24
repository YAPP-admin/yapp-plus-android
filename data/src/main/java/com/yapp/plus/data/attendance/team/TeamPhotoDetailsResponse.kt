package com.yapp.plus.data.attendance.team

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalTime

@Serializable
internal data class TeamPhotoDetailsResponse(
    val photoId: Long,
    val teamId: Long,
    val teamName: String,
    val status: String,
    val imageUrl: String?,
    val imageUrlExpiresAt: String?,
    val uploadedAt: String,
    val uploadedTime: String,
    val submittedAt: String,
    val uploader: TeamPhotoUploaderResponse,
    val selectedMemberCount: Int,
) {
    fun toData(): TeamPhotoDetailsData =
        TeamPhotoDetailsData(
            photoId = photoId,
            teamId = teamId,
            teamName = teamName,
            status = status,
            imageUrl = imageUrl,
            imageExpiresAt = imageUrlExpiresAt?.let(Instant::parse),
            uploadedAt = Instant.parse(uploadedAt),
            uploadedTime = LocalTime.parse(uploadedTime),
            submittedAt = Instant.parse(submittedAt),
            uploaderName = uploader.name,
            selectedMemberCount = selectedMemberCount,
        )
}
