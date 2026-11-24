package com.yapp.plus.domain.attendance

import java.time.Instant
import java.time.LocalTime

data class TeamAttendancePhoto(
    val photoId: Long,
    val teamId: Long,
    val teamName: String,
    val status: TeamAttendancePhotoStatus,
    val image: TeamAttendancePhotoImage?,
    val uploadedAt: Instant,
    val uploadedTime: LocalTime,
    val submittedAt: Instant,
    val uploaderName: String?,
    val selectedMemberCount: Int,
)
