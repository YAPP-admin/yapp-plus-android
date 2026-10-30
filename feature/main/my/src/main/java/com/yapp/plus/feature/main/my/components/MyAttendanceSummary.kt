package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yapp.plus.feature.main.my.R
import com.yapp.plus.feature.main.my.model.MyAttendanceScoreState

@Composable
internal fun MyAttendanceSummary(
    state: MyAttendanceScoreState,
    onRulesClick: () -> Unit,
    onRetry: () -> Unit,
) {
    val content = state as? MyAttendanceScoreState.Content
    val emptyValue = stringResource(R.string.my_empty_value)
    when (state) {
        MyAttendanceScoreState.Loading ->
            MyRequestState(
                message = stringResource(R.string.my_score_loading),
                isLoading = true,
            )
        MyAttendanceScoreState.Error ->
            MyRequestState(
                message = stringResource(R.string.my_score_error),
                onRetry = onRetry,
            )
        MyAttendanceScoreState.Ineligible, is MyAttendanceScoreState.Content ->
            MyStatsCard(
                title = stringResource(R.string.my_attendance_summary),
                valueLabel = stringResource(R.string.my_total_score),
                value =
                    content?.score?.let { stringResource(R.string.my_score, it.totalScore) }
                        ?: emptyValue,
                entries =
                    listOf(
                        stringResource(R.string.my_status_present) to
                            (content?.score?.attendanceCount?.toString() ?: emptyValue),
                        stringResource(R.string.my_status_late) to
                            (content?.score?.lateCount?.toString() ?: emptyValue),
                        stringResource(R.string.my_status_absent) to
                            (content?.score?.absentCount?.toString() ?: emptyValue),
                        stringResource(R.string.my_late_exemption) to
                            (content?.lateExemptionCount?.toString() ?: emptyValue),
                    ),
                modifier = Modifier.padding(horizontal = 20.dp),
                titleAction = {
                    Image(
                        painter = painterResource(R.drawable.ic_attendance_rules),
                        contentDescription = stringResource(R.string.my_attendance_rules),
                        modifier =
                            Modifier.size(20.dp).clickable(
                                role = Role.Button,
                                onClick = onRulesClick,
                            ),
                    )
                },
            )
    }
}
