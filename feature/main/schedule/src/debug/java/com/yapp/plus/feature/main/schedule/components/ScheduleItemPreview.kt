package com.yapp.plus.feature.main.schedule.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleStatusUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleTypeUiModel

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun ScheduleItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        ScheduleItem(
            session =
                ScheduleSessionUiModel(
                    dateLabel = "9.19 (토)",
                    name = "기획 세션",
                    location = "ICT콤플렉스 6층 커뮤니티라운지",
                    timeLabel = "오후 12시 ~ 오후 6시",
                    type = ScheduleTypeUiModel.Regular,
                    status = ScheduleStatusUiModel.Upcoming,
                ),
        )
    }
}
