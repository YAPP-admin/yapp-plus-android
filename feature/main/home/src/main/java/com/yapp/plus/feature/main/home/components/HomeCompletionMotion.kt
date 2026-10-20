package com.yapp.plus.feature.main.home.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing

internal object HomeCompletionMotion {
    const val DURATION_MS = 2_000
    private val easeInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    private val easeOut = CubicBezierEasing(0f, 0f, 0.58f, 1f)
    private val easeIn = CubicBezierEasing(0.42f, 0f, 1f, 1f)
    private val actorTimes = listOf(0, 140, 400, 680, 850, 1150, 1430, 1700, 1900, 2000)
    private val actorEasing =
        listOf(
            easeInOut,
            easeOut,
            easeIn,
            easeInOut,
            easeInOut,
            easeOut,
            easeIn,
            easeInOut,
            easeInOut,
            LinearEasing,
        )
    val actorX =
        actorTrack(listOf(0f, -2.257f, 1.29f, -2.58f, 0f, -1.935f, 0.967f, -2.257f, 0f, 0f))
    val actorY = actorTrack(listOf(0f, 5.961f, -9.98f, 5.961f, 0f, 4.769f, -7.884f, 5.365f, 0f, 0f))
    val actorScaleX =
        actorTrack(listOf(1f, 1.035f, 0.98f, 1.04f, 1f, 1.03f, 0.985f, 1.035f, 1f, 1f))
    val actorScaleY = actorTrack(listOf(1f, 0.95f, 1.025f, 0.95f, 1f, 0.96f, 1.02f, 0.955f, 1f, 1f))
    val actorAlpha = listOf(frame(0, 0f, easeOut), frame(180, 1f), frame(2000, 1f))
    val leftAlpha = listOf(frame(0, 0f), frame(140, 0f, easeOut), frame(380, 1f), frame(2000, 1f))
    val rightAlpha = listOf(frame(0, 0f), frame(240, 0f, easeOut), frame(480, 1f), frame(2000, 1f))
    val leftY =
        listOf(
            frame(0, 7f),
            frame(140, 7f, easeOut),
            frame(560, -2f, easeInOut),
            frame(950, 0f, easeInOut),
            frame(1600, -3f, easeInOut),
            frame(2000, 0f),
        )
    val rightY =
        listOf(
            frame(0, 7f),
            frame(240, 7f, easeOut),
            frame(660, -2f, easeInOut),
            frame(950, 0f, easeInOut),
            frame(1600, -3f, easeInOut),
            frame(2000, 0f),
        )
    val leftRotation =
        listOf(
            frame(0, -0.319f, easeOut),
            frame(640, -0.232f, easeInOut),
            frame(1300, -0.284f, easeInOut),
            frame(2000, -0.267f),
        )
    val rightRotation =
        listOf(
            frame(0, 0.326f, easeOut),
            frame(740, 0.239f, easeInOut),
            frame(1300, 0.291f, easeInOut),
            frame(2000, 0.274f),
        )

    fun sample(
        timeMs: Float,
        frames: List<HomeMotionFrame>,
    ): Float {
        val endIndex = frames.indexOfFirst { timeMs <= it.timeMs }
        if (endIndex <= 0) return frames[if (endIndex == 0) 0 else frames.lastIndex].value
        val start = frames[endIndex - 1]
        val end = frames[endIndex]
        val fraction = (timeMs - start.timeMs) / (end.timeMs - start.timeMs)
        return start.value + (end.value - start.value) * start.easing.transform(fraction)
    }

    private fun actorTrack(values: List<Float>): List<HomeMotionFrame> =
        actorTimes.mapIndexed { index, time ->
            frame(time, values[index], actorEasing[index])
        }

    private fun frame(
        timeMs: Int,
        value: Float,
        easing: Easing = LinearEasing,
    ): HomeMotionFrame = HomeMotionFrame(timeMs, value, easing)
}
