package com.yapp.plus.feature.main.my.model

data class MySessionSummary(
    val totalSessionCount: Int,
    val remainingSessionCount: Int,
    val progressPercent: Int? = null,
)
