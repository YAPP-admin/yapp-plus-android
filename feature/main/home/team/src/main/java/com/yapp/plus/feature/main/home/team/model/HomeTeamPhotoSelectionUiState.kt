package com.yapp.plus.feature.main.home.team.model

import androidx.annotation.StringRes

data class HomeTeamPhotoSelectionUiState(
    val members: List<HomeTeamPhotoMemberUiModel>,
    val canConfirm: Boolean,
    val isSubmitting: Boolean = false,
    @param:StringRes val errorResource: Int? = null,
) {
    val selectedCount: Int
        get() = members.count { it.isSelected }
}
