package com.yapp.plus.feature.main.home.team.model

data class HomeTeamPhotoReceiptUiModel(
    val teamName: String,
    val submittedTimeLabel: String,
    val attendeeCount: Int,
    val teamMemberCount: Int,
    val attendeeNamesLabel: String,
    val uploadTimeLabel: String? = null,
)
