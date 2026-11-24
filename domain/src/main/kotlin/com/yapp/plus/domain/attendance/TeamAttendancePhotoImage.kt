package com.yapp.plus.domain.attendance

import java.time.Instant

data class TeamAttendancePhotoImage(
    val url: String,
    val expiresAt: Instant,
)
