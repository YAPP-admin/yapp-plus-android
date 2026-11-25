package com.yapp.plus.feature.main.home.model

internal data class HomeTeamPhotoReceiptUiModel(
    val teamName: String,
    val submittedTimeLabel: String,
    val attendeeCount: Int,
    val teamMemberCount: Int,
    val attendeeNamesLabel: String,
    val uploadTimeLabel: String? = null,
)
