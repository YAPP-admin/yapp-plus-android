package com.yapp.plus.feature.setting

data class NotificationSettingsUiState(
    val isEnabled: Boolean? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val hasLoadError: Boolean = false,
    val hasUpdateError: Boolean = false,
    val isPermissionGranted: Boolean = true,
)
