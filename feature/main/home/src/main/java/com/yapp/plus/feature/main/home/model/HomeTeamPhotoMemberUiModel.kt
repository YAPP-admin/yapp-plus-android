package com.yapp.plus.feature.main.home.model

internal data class HomeTeamPhotoMemberUiModel(
    val membershipId: Long,
    val name: String,
    val roleLabel: String? = null,
    val isCurrentUser: Boolean = false,
    val isSelected: Boolean = false,
)
