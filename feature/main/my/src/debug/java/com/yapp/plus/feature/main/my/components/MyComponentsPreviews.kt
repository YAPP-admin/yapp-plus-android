package com.yapp.plus.feature.main.my.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.feature.main.my.MyPreviewData
import com.yapp.plus.feature.main.my.R
import com.yapp.plus.feature.main.my.model.MyAttendanceStatus

@Preview @Composable
private fun SummaryPreview() {
    YappTheme { MyAttendanceSummary(MyPreviewData.score, {}, {}) }
}

@Preview @Composable
private fun StatsCardPreview() {
    YappTheme { MyStatsCard("세션 현황", "진행률", "50%", listOf("총 세션" to "12회", "남은 세션" to "6회")) }
}

@Preview @Composable
private fun StatValuePreview() {
    YappTheme { MyStatValue("출석", "3") }
}

@Preview @Composable
private fun BadgePreview() {
    YappTheme { MyAttendanceBadge(MyAttendanceStatus.Absent) }
}

@Preview @Composable
private fun HistoryRowPreview() {
    YappTheme { MyHistoryRow(MyPreviewData.history.items.first()) }
}

@Preview @Composable
private fun EmptyPreview() {
    YappTheme { MyHistoryEmpty() }
}

@Preview @Composable
private fun WarningPreview() {
    YappTheme { MyAbsenceWarning() }
}

@Preview @Composable
private fun PaginationPreview() {
    YappTheme { MyHistoryPagination(MyPreviewData.history.copy(hasLoadMoreError = true), {}) }
}

@Preview @Composable
private fun RequestStatePreview() {
    YappTheme { MyRequestState("출석 현황을 불러오지 못했어요", onRetry = {}) }
}

@Preview @Composable
private fun RulesDialogPreview() {
    YappTheme { MyAttendanceRulesDialog({}) }
}

@Preview @Composable
private fun RuleGroupPreview() {
    YappTheme {
        AttendanceRuleGroup(R.string.my_rule_one_hour, R.string.my_rule_four, R.string.my_rule_ten)
    }
}

@Preview @Composable
private fun RuleRowPreview() {
    YappTheme { AttendanceRuleRow("1시간 지각/조퇴", "-4점") }
}

@Preview @Composable
private fun RulesNoticePreview() {
    YappTheme { AttendanceRulesNotice() }
}
