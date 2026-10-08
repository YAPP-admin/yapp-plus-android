package com.yapp.plus.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun AttendanceMyScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        AttendanceHeader(
            title = stringResource(R.string.attendance_my_title),
            iconResource = DesignSystemR.drawable.ic_settings,
            iconDescription = stringResource(R.string.attendance_my_settings),
            onIconClick = onSettingsClick,
        )
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp),
            ) {
                AttendanceProfileCard(
                    name = stringResource(R.string.attendance_my_name),
                    role = stringResource(R.string.attendance_my_role),
                    generation = stringResource(R.string.attendance_my_generation),
                )
                AttendanceSummaryCard()
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(YappMainColor.background),
            )
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp),
            ) {
                AttendanceMenuItem(
                    title = stringResource(R.string.attendance_my_history),
                    onClick = onHistoryClick,
                )
                AttendanceMenuItem(
                    title = stringResource(R.string.attendance_my_support),
                    onClick = onSupportClick,
                )
                DemoDataLabel(modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))
            }
        }
    }
}
