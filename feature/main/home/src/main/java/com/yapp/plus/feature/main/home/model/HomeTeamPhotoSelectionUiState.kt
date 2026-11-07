package com.yapp.plus.feature.main.home.model

import androidx.annotation.StringRes

internal data class HomeTeamPhotoSelectionUiState(
    val members: List<HomeTeamPhotoMemberUiModel>,
    val canConfirm: Boolean,
    val isSubmitting: Boolean = false,
    @param:StringRes val errorResource: Int? = null,
) {
    val selectedCount: Int
        get() = members.count { it.isSelected }
}
