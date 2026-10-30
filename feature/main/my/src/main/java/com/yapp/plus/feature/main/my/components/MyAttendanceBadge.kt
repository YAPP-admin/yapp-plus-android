package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.model.MyAttendanceStatus

@Composable
internal fun MyAttendanceBadge(status: MyAttendanceStatus) {
    val isCritical = status == MyAttendanceStatus.Absent || status == MyAttendanceStatus.Rejected
    val isWarning =
        status in
            listOf(
                MyAttendanceStatus.Late,
                MyAttendanceStatus.EarlyLeave,
                MyAttendanceStatus.LateAndEarlyLeave,
            )
    val background =
        when {
            isCritical -> YappMainColor.criticalWeak
            isWarning -> YappMainColor.warningWeak
            else -> YappMainColor.basement
        }
    val foreground =
        when {
            isCritical -> YappMainColor.critical
            isWarning -> YappMainColor.warning
            else -> YappMainColor.textSecondary
        }
    Surface(color = background, shape = RoundedCornerShape(4.dp)) {
        YappText(
            text = stringResource(status.label),
            style = YappTypography.scaleT2Bold,
            color = foreground,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
