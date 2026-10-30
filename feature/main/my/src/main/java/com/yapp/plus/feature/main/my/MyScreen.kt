package com.yapp.plus.feature.main.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.feature.main.my.components.MyAttendanceRulesDialog
import com.yapp.plus.feature.main.my.components.MyAttendanceSummary
import com.yapp.plus.feature.main.my.components.MyMenuRow
import com.yapp.plus.feature.main.my.components.MyProfile
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MyUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun MyScreen(
    state: MyUiState = MyUiState(),
    modifier: Modifier = Modifier,
    onRetryScore: () -> Unit = {},
    onAttendanceHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onContactClick: () -> Unit = {},
) {
    var showRules by rememberSaveable { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(YappMainColor.surface)) {
        YappMainHeader(
            title = stringResource(R.string.my_title),
            iconResource = DesignSystemR.drawable.ic_settings,
            iconDescription = stringResource(R.string.my_settings),
            onIconClick = onSettingsClick,
            iconTint = YappMainColor.textSubtle,
        )
        LazyColumn(Modifier.weight(1f)) {
            item {
                MyProfile((state.profile as? MyProfileUiState.Content)?.profile)
                Spacer(Modifier.height(2.dp))
                MyAttendanceSummary(state.score, { showRules = true }, onRetryScore)
                Spacer(Modifier.height(28.dp))
                Spacer(Modifier.fillMaxWidth().height(8.dp).background(YappMainColor.basement))
                Spacer(Modifier.height(10.dp))
            }
            item {
                MyMenuRow(
                    title = stringResource(R.string.my_attendance_history_title),
                    onClick = onAttendanceHistoryClick,
                )
            }
            item {
                MyMenuRow(stringResource(R.string.my_contact), onContactClick)
            }
        }
    }
    if (showRules) MyAttendanceRulesDialog(onDismiss = { showRules = false })
}
