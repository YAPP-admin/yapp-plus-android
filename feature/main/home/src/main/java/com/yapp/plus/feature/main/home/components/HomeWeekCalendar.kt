package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.core.ui.formatter.AppDateFormatter
import com.yapp.plus.feature.main.home.R
import java.time.LocalDate

private val standardCalendarDayHeight = 54.dp
private val attendedCalendarDayHeight = 58.dp
private val attendedWeekdayStyle = YappHomeTypography.day.copy(fontWeight = FontWeight.Medium)

@Composable
internal fun HomeWeekCalendar(
    weekStartDate: LocalDate,
    sessionDate: LocalDate?,
    today: LocalDate,
    modifier: Modifier = Modifier,
    isSessionAttended: Boolean = false,
    dayHeight: Dp = if (isSessionAttended) attendedCalendarDayHeight else standardCalendarDayHeight,
) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        repeat(DAYS_IN_WEEK) { index ->
            val date = weekStartDate.plusDays(index.toLong())
            HomeCalendarDay(
                date = date,
                sessionDate = sessionDate,
                today = today,
                modifier = Modifier.weight(1f).height(dayHeight),
                hasLeadingBorder = index == 0,
                isAttended = isSessionAttended && date == sessionDate,
                dateSpacing = if (isSessionAttended) 2.dp else 4.dp,
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
    isAttended: Boolean = false,
    dateSpacing: Dp = if (isAttended) 2.dp else 4.dp,
) {
    val isToday = date == today
    val color =
        when {
            isToday -> YappHomeColor.surface
            date == sessionDate -> YappHomeColor.brand
            else -> YappHomeColor.muted
        }
    Box(
        modifier =
            modifier.height(
                if (isAttended) attendedCalendarDayHeight else standardCalendarDayHeight,
            ),
    ) {
        val divider = YappHomeColor.divider
        Canvas(Modifier.fillMaxSize()) {
            drawCalendarSeparator(size.width, divider)
            if (hasLeadingBorder) drawCalendarSeparator(0f, divider)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dateSpacing),
        ) {
            YappText(
                text = date.format(AppDateFormatter.dayOfWeek),
                style = if (isAttended) attendedWeekdayStyle else YappHomeTypography.day,
                color = YappHomeColor.subtle,
            )
            if (isAttended) {
                HomeAttendedDate(dayOfMonth = date.dayOfMonth)
                return@Column
            }
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

@Composable
private fun HomeAttendedDate(dayOfMonth: Int) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_home_attendance_checked_in),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        YappText(dayOfMonth.toString(), YappTypography.scaleT2Medium, YappHomeColor.surface)
    }
    YappText(
        text = stringResource(R.string.home_attendance_marker),
        style = YappHomeTypography.day,
        color = YappHomeColor.brand,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(YappMainColor.brandWeakSurface),
        textAlign = TextAlign.Center,
    )
}

private const val DAYS_IN_WEEK = 7

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
