package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.feature.main.home.HomePreviewData
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeNoticeUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.ReportChoice
import java.time.LocalDate

@Preview(widthDp = 321)
@Composable
fun HomeCalendarPreview() {
    YappTheme {
        HomeWeekCalendar(
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 3),
        )
    }
}

@Preview(widthDp = 44)
@Composable
fun HomeCalendarDayPreview() {
    YappTheme { HomeCalendarDay(LocalDate.of(2026, 11, 3), null, LocalDate.of(2026, 11, 3)) }
}

@Preview(widthDp = 361)
@Composable
fun HomeQuickActionsPreview() {
    YappTheme { HomeQuickActions(hasAttended = false, canReport = true, {}, {}, {}) }
}

@Preview(widthDp = 176)
@Composable
fun HomeQuickActionPreview() {
    YappTheme {
        HomeQuickAction(
            stringResource(R.string.home_all_sessions),
            stringResource(R.string.home_check),
            R.drawable.home_action_sessions,
            {},
        )
    }
}

@Preview(widthDp = 361, heightDp = 253)
@Composable
fun HomeEmptyStatePreview() {
    YappTheme { HomeEmptyState(stringResource(R.string.home_notices_empty)) }
}

@Preview(widthDp = 361)
@Composable
fun HomeNoticeListPreview() {
    YappTheme { HomeNoticeList(HomePreviewData.notices(), {}) }
}

@Preview(widthDp = 361, heightDp = 160)
@Composable
fun HomeNoticeContentPreview() {
    YappTheme { HomeNoticeContent(HomeNoticeUiState.Error, {}, {}) }
}

@Preview(widthDp = 361)
@Composable
fun HomeNoticeCardPreview() {
    YappTheme { HomeNoticeCard(HomePreviewData.notices().first(), {}) }
}

@Preview
@Composable
fun HomeReportTipPreview() {
    YappTheme { HomeReportTip() }
}

@Preview(widthDp = 393)
@Composable
fun HomeTopBarPreview() {
    YappTheme { HomeTopBar(stringResource(R.string.attendance_home_notifications), {}) }
}

@Preview(widthDp = 337)
@Composable
fun HomeCodeInputPreview() {
    YappTheme { HomeCodeInput("1", {}, isError = false, enabled = true, onConfirm = {}) }
}

@Preview(widthDp = 54)
@Composable
fun HomeCodeDigitPreview() {
    YappTheme { HomeCodeDigit('1', isActive = false, isError = true) }
}

@Preview(widthDp = 337)
@Composable
fun HomeReportOptionPreview() {
    YappTheme {
        HomeReportOption(
            stringResource(R.string.home_report_absent),
            isSelected = true,
            enabled = true,
            onClick = {},
        )
    }
}

@Preview(widthDp = 373)
@Composable
fun HomeSheetPreview(
    @PreviewParameter(HomeSheetPreviewParameterProvider::class) sheet: HomeSheet,
) {
    val state = HomeInteractionUiState(sheet = sheet, reportChoice = ReportChoice.Absent)
    YappTheme { HomeSheetContent(state, {}, {}, {}, {}, {}, Modifier.padding(18.dp)) }
}

@Preview(widthDp = 337)
@Composable
fun HomeSheetHeadingPreview() {
    YappTheme { HomeSheetHeading(HomeInteractionUiState(sheet = HomeSheet.AttendanceCode)) }
}

@Preview(widthDp = 337)
@Composable
fun HomeReportChoicesPreview() {
    YappTheme { ReportChoices(HomeInteractionUiState(reportChoice = ReportChoice.Absent), {}) }
}

@Preview(widthDp = 337)
@Composable
fun HomeLateDurationChoicesPreview() {
    YappTheme {
        LateDurationChoices(
            HomeInteractionUiState(reportType = AttendanceReportType.ONE_HOUR_LATE),
            {},
        )
    }
}

@Preview
@Composable
fun HomeSubmissionErrorPreview() {
    YappTheme { HomeSubmissionError(stringResource(R.string.home_code_invalid)) }
}

@Preview(widthDp = 361)
@Composable
fun HomeSessionContentPreview() {
    YappTheme {
        HomeSessionContent(
            HomePreviewData.session(),
            hasAttended = false,
            hasReported = false,
            now = HomePreviewData.now,
            onAttendanceClick = {},
        )
    }
}

@Preview(widthDp = 361)
@Composable
fun HomeSessionErrorPreview() {
    YappTheme { HomeSessionError({}) }
}

@Preview(widthDp = 350, heightDp = 180)
@Composable
fun HomeCompletionGraphicPreview() {
    YappTheme { HomeCompletionGraphic() }
}

@Preview(widthDp = 150, heightDp = 150)
@Composable
fun HomeConfettiPreview() {
    YappTheme { HomeConfetti(isLeft = true, scale = 1f, time = 1000f, canAnimate = true) }
}
