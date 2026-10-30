package com.yapp.plus.domain.attendance

import java.time.Instant

data class TeamPhotoSubmission(
    val photoId: Long,
    val submittedAt: Instant,
)
