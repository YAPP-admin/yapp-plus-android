package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheet
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeSubmittedReportSheet(
    type: AttendanceReportType,
    reason: String?,
    onDismiss: () -> Unit,
) {
    YappBottomSheet(
        onDismissRequest = onDismiss,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        title = null,
        body = null,
        confirmText = null,
        dismissText = null,
        showHandle = true,
        showActions = false,
        variant = YappBottomSheetVariant.Home,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        ) {
            YappText(
                text = stringResource(R.string.home_report_submitted_title),
                style = YappTypography.scaleT7Bold,
                color = YappMainColor.textPrimary,
            )
            Spacer(Modifier.height(20.dp))
            YappText(
                text = stringResource(reportTypeLabel(type)),
                style = YappTypography.scaleT4Bold,
                color = YappMainColor.brand,
                modifier =
                    Modifier
                        .background(YappHomeColor.reportBadge, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 5.5.dp),
            )
            Spacer(Modifier.height(10.dp))
            YappText(
                text = reason ?: stringResource(R.string.home_report_reason_missing),
                style = YappTypography.body2NormalRegular.copy(letterSpacing = 0.sp),
                color = YappMainColor.textPrimary,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 94.dp)
                        .background(YappMainColor.basement, RoundedCornerShape(12.dp))
                        .padding(16.dp),
            )
            Spacer(Modifier.height(24.dp))
            YappButton(
                text = stringResource(R.string.home_confirm),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                variant = YappButtonVariant.BrandAction,
                size = YappButtonSize.MainAction,
            )
        }
    }
}

private fun reportTypeLabel(type: AttendanceReportType): Int =
    when (type) {
        AttendanceReportType.LATE -> R.string.home_report_late
        AttendanceReportType.EARLY_LEAVE -> R.string.home_report_early_leave
        AttendanceReportType.ABSENT -> R.string.home_report_absent
    }
