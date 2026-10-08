package com.yapp.plus.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun AttendanceHistoryItem(
    title: String,
    date: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            YappText(title, YappTypography.body2NormalMedium, YappMainColor.textPrimary)
            YappText(date, YappTypography.label2Regular, YappMainColor.textSecondary)
        }
        YappText(
            text = stringResource(R.string.attendance_demo_status),
            style = YappTypography.caption1Bold,
            color = YappMainColor.brand,
            modifier =
                Modifier
                    .background(YappMainColor.brandWeak, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}
