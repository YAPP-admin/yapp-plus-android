package com.yapp.plus.core.designsystem.components.popup

enum class YappAlertDialogVariant(
    internal val isCompact: Boolean,
) {
    Default(isCompact = false),
    Compact(isCompact = true),
    AuthError(isCompact = true),
}
