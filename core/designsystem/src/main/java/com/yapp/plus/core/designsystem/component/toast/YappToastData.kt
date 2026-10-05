package com.yapp.plus.core.designsystem.component.toast

import androidx.compose.runtime.Immutable

private const val DEFAULT_TOAST_DURATION_MILLIS = 2_000L

@Immutable
data class YappToastData(
    val text: String,
    val type: YappToastType = YappToastType.Default,
    val color: YappToastColor = YappToastColor.Dark,
    val showIcon: Boolean = true,
    val durationMillis: Long = DEFAULT_TOAST_DURATION_MILLIS,
)
