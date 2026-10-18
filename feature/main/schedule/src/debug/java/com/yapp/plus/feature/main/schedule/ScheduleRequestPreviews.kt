package com.yapp.plus.feature.main.schedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleStatusUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleTypeUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState

@Preview(name = "Schedule loading", widthDp = 393, heightDp = 692)
@Composable
private fun ScheduleLoadingPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        ScheduleScreen(state = ScheduleUiState.Loading)
    }
}

@Preview(name = "Schedule empty", widthDp = 393, heightDp = 692)
@Composable
private fun ScheduleEmptyPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        ScheduleScreen(state = ScheduleUiState.Empty)
    }
}

@Preview(name = "Schedule error", widthDp = 393, heightDp = 692)
@Composable
private fun ScheduleErrorPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        ScheduleScreen(state = ScheduleUiState.Error)
    }
}

@Preview(name = "Schedule content", widthDp = 393, heightDp = 692)
@Composable
private fun ScheduleContentPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        ScheduleScreen(
            state = ScheduleUiState.Content(listOf(sampleScheduleSession())),
        )
    }
}

@Composable
private fun sampleScheduleSession() =
    ScheduleSessionUiModel(
        dateLabel = stringResource(R.string.schedule_date_second),
        name = stringResource(R.string.schedule_planning),
        location = stringResource(R.string.schedule_place_second),
        timeLabel = stringResource(R.string.schedule_time_second),
        type = ScheduleTypeUiModel.Regular,
        status = ScheduleStatusUiModel.Upcoming,
    )
