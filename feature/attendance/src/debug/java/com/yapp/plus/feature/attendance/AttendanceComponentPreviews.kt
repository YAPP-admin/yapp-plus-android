package com.yapp.plus.feature.attendance

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Preview(name = "Home session card", widthDp = 361)
@Composable
private fun HomeSessionCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { HomeSessionCard() }
}

@Preview(name = "Schedule detail", widthDp = 250, showBackground = true)
@Composable
private fun AttendanceDetailPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceDetail(
            text = stringResource(R.string.attendance_schedule_place_first),
            iconResource = DesignSystemR.drawable.ic_map_pin,
        )
    }
}

@Preview(name = "Schedule item", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceScheduleItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceScheduleItem(
            date = stringResource(R.string.attendance_schedule_date_second),
            title = stringResource(R.string.attendance_schedule_planning),
            place = stringResource(R.string.attendance_schedule_place_second),
            time = stringResource(R.string.attendance_schedule_time_second),
            isPast = false,
            isOnline = false,
        )
    }
}

@Preview(name = "Board category selector", widthDp = 353, showBackground = true)
@Composable
private fun BoardCategorySelectorPreview(
    @PreviewParameter(BoardCategoryPreviewParameterProvider::class) category: BoardCategory,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        BoardCategorySelector(selectedCategory = category, onCategorySelected = {})
    }
}

@Preview(name = "Board category tab", showBackground = true)
@Composable
private fun BoardCategoryChipPreview(
    @PreviewParameter(BoardCategoryPreviewParameterProvider::class) category: BoardCategory,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Row {
            BoardCategoryChip(category = category, isSelected = true, onClick = {})
            BoardCategoryChip(category = category, isSelected = false, onClick = {})
        }
    }
}

@Preview(name = "Board empty state", widthDp = 353, heightDp = 240, showBackground = true)
@Composable
private fun AttendanceBoardEmptyStatePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceBoardEmptyState() }
}

@Preview(name = "Profile", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceProfileCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceProfileCard(
            name = stringResource(R.string.attendance_my_name),
            role = stringResource(R.string.attendance_my_role),
            generation = stringResource(R.string.attendance_my_generation),
        )
    }
}

@Preview(name = "Attendance summary", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceSummaryCardPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { AttendanceSummaryCard() }
}

@Preview(name = "Attendance statistic", widthDp = 85, showBackground = true)
@Composable
private fun AttendanceStatisticPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceStatistic(label = stringResource(R.string.attendance_my_present), count = 3)
    }
}

@Preview(name = "My menu item", widthDp = 353, showBackground = true)
@Composable
private fun AttendanceMenuItemPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AttendanceMenuItem(title = stringResource(R.string.attendance_my_history))
    }
}
