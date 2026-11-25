package com.yapp.plus.core.ui.formatter

import android.content.Context
import androidx.annotation.StringRes
import java.time.format.DateTimeFormatter
import java.util.Locale

object AppDateFormatter {
    private val locale: Locale = Locale.KOREAN

    fun ofPatternRes(
        context: Context,
        @StringRes resId: Int,
    ): DateTimeFormatter = DateTimeFormatter.ofPattern(context.getString(resId), locale)

    val monthDay: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("M월 d일", locale)
    }

    val monthDayDayOfWeek: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("M월 d일 (E)", locale)
    }

    val timeAmPmHour: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("a h시", locale)
    }

    val dayOfMonthTime: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("d'일' HH:mm", locale)
    }

    val dayOfWeek: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("E", locale)
    }

    val timeHourMinute: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("HH:mm", locale)
    }

    val monthDayDot: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("M.d", locale)
    }

    val yearMonthDayKoreanDayOfWeek: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", locale)
    }

    val yearMonthDayDotDayOfWeek: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", locale)
    }

    val monthDayDotDayOfWeek: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("M.d (E)", locale)
    }
}
