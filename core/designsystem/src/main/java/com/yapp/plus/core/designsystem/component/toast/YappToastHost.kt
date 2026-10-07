package com.yapp.plus.core.designsystem.component.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private const val TOAST_ANIMATION_DURATION_MILLIS = 200L
private const val IME_TOAST_BOTTOM_PADDING_DP = 16

@Composable
fun YappToastHost(
    toastManager: YappToastManager,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 80.dp,
) {
    var currentToast by remember(toastManager) { mutableStateOf<YappToastData?>(null) }
    var isVisible by remember(toastManager) { mutableStateOf(false) }
    val accessibilityManager = LocalAccessibilityManager.current
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    val toastBottomPadding = if (isImeVisible) IME_TOAST_BOTTOM_PADDING_DP.dp else bottomPadding

    LaunchedEffect(toastManager, accessibilityManager) {
        toastManager.toasts.collectLatest { toast ->
            currentToast = toast
            isVisible = true
            val recommendedTimeout = accessibilityManager?.calculateRecommendedTimeoutMillis(
                originalTimeoutMillis = toast.durationMillis,
                containsIcons = toast.showIcon,
                containsText = true,
                containsControls = false,
            ) ?: toast.durationMillis

            delay(recommendedTimeout)
            isVisible = false
            delay(TOAST_ANIMATION_DURATION_MILLIS)
            if (currentToast == toast) currentToast = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = isVisible && currentToast != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(TOAST_ANIMATION_DURATION_MILLIS.toInt()),
            ) + fadeIn(tween(TOAST_ANIMATION_DURATION_MILLIS.toInt())),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(TOAST_ANIMATION_DURATION_MILLIS.toInt()),
            ) + fadeOut(tween(TOAST_ANIMATION_DURATION_MILLIS.toInt())),
        ) {
            currentToast?.let { toast ->
                YappToast(
                    text = toast.text,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = toastBottomPadding)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    color = toast.color,
                    type = toast.type,
                    showIcon = toast.showIcon,
                )
            }
        }
    }
}
