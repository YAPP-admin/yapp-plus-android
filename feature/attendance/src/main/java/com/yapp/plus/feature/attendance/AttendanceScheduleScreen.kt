package com.yapp.plus.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappMainColor

@Composable
fun AttendanceScheduleScreen(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        AttendanceHeader(title = stringResource(R.string.attendance_schedule_title))
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            val dates =
                listOf(
                    R.string.attendance_schedule_date_first,
                    R.string.attendance_schedule_date_second,
                    R.string.attendance_schedule_date_third,
                    R.string.attendance_schedule_date_fourth,
                    R.string.attendance_schedule_date_fifth,
                    R.string.attendance_schedule_date_sixth,
                )
            for (index in dates.indices) {
                val title =
                    when (index) {
                        1 -> R.string.attendance_schedule_planning
                        4 -> R.string.attendance_schedule_user_test
                        else -> R.string.attendance_schedule_team
                    }
                val place =
                    when (index) {
                        0 -> R.string.attendance_schedule_place_first
                        1 -> R.string.attendance_schedule_place_second
                        4 -> R.string.attendance_schedule_place_fifth
                        else -> R.string.attendance_schedule_place_online
                    }
                val time =
                    when (index) {
                        1 -> R.string.attendance_schedule_time_second
                        4 -> R.string.attendance_schedule_time_fifth
                        else -> R.string.attendance_schedule_time
                    }
                AttendanceScheduleItem(
                    date = stringResource(dates[index]),
                    title = stringResource(title),
                    place = stringResource(place),
                    time = stringResource(time),
                    isPast = index == 0,
                    isOnline = index != 1 && index != 4,
                )
            }
        }
    }
}
