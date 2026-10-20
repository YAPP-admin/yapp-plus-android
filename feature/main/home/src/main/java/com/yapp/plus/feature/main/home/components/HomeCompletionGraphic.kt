package com.yapp.plus.feature.main.home.components

import android.animation.ValueAnimator
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeCompletionGraphic(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val canAnimate =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ValueAnimator.areAnimatorsEnabled()
        } else {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) >
                0f
        }
    val time =
        when {
            LocalInspectionMode.current -> 1_000f
            canAnimate -> completionTime()
            else -> 0f
        }
    BoxWithConstraints(modifier.widthIn(max = 350.dp).fillMaxWidth().aspectRatio(350f / 180f)) {
        val scale = maxWidth.value / 350f
        Box(
            Modifier
                .offset((97.2655f * scale).dp, (27.6372f * scale).dp)
                .size((143.3628f * scale).dp)
                .clip(CircleShape)
                .background(YappHomeColor.celebration),
        ) {
            Image(
                painterResource(R.drawable.home_completion_actor),
                null,
                Modifier
                    .offset((7.1909f * scale).dp, (31.9269f * scale).dp)
                    .size((128.982f * scale).dp, (119.2128f * scale).dp)
                    .graphicsLayer {
                        if (canAnimate) {
                            alpha =
                                HomeCompletionMotion.sample(time, HomeCompletionMotion.actorAlpha)
                            translationX =
                                HomeCompletionMotion.sample(time, HomeCompletionMotion.actorX) *
                                scale *
                                density
                            translationY =
                                HomeCompletionMotion.sample(time, HomeCompletionMotion.actorY) *
                                scale *
                                density
                            scaleX =
                                HomeCompletionMotion.sample(time, HomeCompletionMotion.actorScaleX)
                            scaleY =
                                HomeCompletionMotion.sample(time, HomeCompletionMotion.actorScaleY)
                        }
                    },
            )
        }
        HomeConfetti(isLeft = true, scale = scale, time = time, canAnimate = canAnimate)
        HomeConfetti(isLeft = false, scale = scale, time = time, canAnimate = canAnimate)
    }
}

@Composable
internal fun completionTime(): Float {
    val transition = rememberInfiniteTransition(label = "attendance celebration")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = HomeCompletionMotion.DURATION_MS.toFloat(),
        animationSpec =
            infiniteRepeatable(
                animation = tween(HomeCompletionMotion.DURATION_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shared Figma timeline",
    )
    return time
}

@Composable
internal fun HomeConfetti(
    isLeft: Boolean,
    scale: Float,
    time: Float,
    canAnimate: Boolean,
) {
    val motion = HomeCompletionMotion
    val resource = if (isLeft) R.drawable.home_completion_left else R.drawable.home_completion_right
    val x = if (isLeft) 4.9725f else 210.79f
    val y = if (isLeft) 28.2203f else 23.792f
    val width = if (isLeft) 108.6873f else 121.916f
    val height = if (isLeft) 112.0202f else 116.0108f
    Image(
        painterResource(resource),
        null,
        Modifier
            .offset((x * scale).dp, (y * scale).dp)
            .size((width * scale).dp, (height * scale).dp)
            .graphicsLayer {
                if (canAnimate) {
                    val opacity = if (isLeft) motion.leftAlpha else motion.rightAlpha
                    val translate = if (isLeft) motion.leftY else motion.rightY
                    val rotation = if (isLeft) motion.leftRotation else motion.rightRotation
                    val baseRotation = if (isLeft) -0.319f else 0.326f
                    alpha = motion.sample(time, opacity)
                    translationY = motion.sample(time, translate) * scale * density
                    rotationZ =
                        (motion.sample(time, rotation) - baseRotation) *
                        RADIANS_TO_DEGREES
                }
            },
    )
}

private const val RADIANS_TO_DEGREES = 57.29578f
