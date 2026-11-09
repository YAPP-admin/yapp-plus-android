package com.yapp.plus.feature.setting

data class NotificationSettingsUiState(
    val isEnabled: Boolean? = null,
    val isUpdating: Boolean = false,
    val hasError: Boolean = false,
    val isPermissionGranted: Boolean = true,
)
