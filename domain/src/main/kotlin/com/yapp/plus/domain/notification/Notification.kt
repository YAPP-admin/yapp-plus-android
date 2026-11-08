package com.yapp.plus.domain.notification

import java.time.LocalDate

data class Notification(
    val id: Long,
    val content: String,
    val sentDate: LocalDate,
    val postId: Long?,
)
