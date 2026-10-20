package com.yapp.plus.feature.main.home.components

import androidx.compose.animation.core.Easing

internal data class HomeMotionFrame(
    val timeMs: Int,
    val value: Float,
    val easing: Easing,
)
