package com.yapp.plus.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun AttendanceHistoryCard(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(YappMainColor.surface, RoundedCornerShape(16.dp))
                .padding(20.dp),
    ) {
        YappText(
            text = stringResource(R.string.attendance_home_history_title),
            style = YappTypography.heading2Bold,
            color = YappMainColor.textPrimary,
        )
        Spacer(Modifier.height(16.dp))
        AttendanceHistoryItem(
            title = stringResource(R.string.attendance_home_history_session),
            date = stringResource(R.string.attendance_home_history_date),
        )
    }
}
