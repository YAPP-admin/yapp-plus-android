package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun HomeWeekCalendar(
    weekStartDate: LocalDate,
    sessionDate: LocalDate?,
    today: LocalDate,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        repeat(DAYS_IN_WEEK) { index ->
            HomeCalendarDay(
                date = weekStartDate.plusDays(index.toLong()),
                sessionDate = sessionDate,
                today = today,
                modifier = Modifier.weight(1f),
                hasLeadingBorder = index == 0,
            )
        }
    }
}

@Composable
internal fun HomeCalendarDay(
    date: LocalDate,
    sessionDate: LocalDate?,
    today: LocalDate,
    modifier: Modifier = Modifier,
    hasLeadingBorder: Boolean = false,
) {
    val isToday = date == today
    val color =
        when {
            isToday -> YappHomeColor.surface
            date == sessionDate -> YappHomeColor.brand
            else -> YappHomeColor.muted
        }
    Box(modifier = modifier.height(54.dp)) {
        val divider = YappHomeColor.divider
        Canvas(Modifier.fillMaxSize()) {
            drawCalendarSeparator(size.width, divider)
            if (hasLeadingBorder) drawCalendarSeparator(0f, divider)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            YappText(date.format(dayFormatter), YappHomeTypography.day, YappHomeColor.subtle)
            Box(
                modifier =
                    Modifier.size(20.dp).background(
                        if (isToday) YappHomeColor.action else Color.Transparent,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                YappText(date.dayOfMonth.toString(), YappHomeTypography.caption, color)
            }
        }
    }
}

private const val DAYS_IN_WEEK = 7
private val dayFormatter = DateTimeFormatter.ofPattern("E", Locale.KOREAN)

private fun DrawScope.drawCalendarSeparator(
    x: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x, 0f),
        end = Offset(x, size.height),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx())),
    )
}
