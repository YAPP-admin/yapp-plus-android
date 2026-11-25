package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.input.YappTextField
import com.yapp.plus.core.designsystem.components.input.YappTextFieldVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.modifier.noRippleSelectable
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet

@Composable
internal fun HomeReportSheet(
    state: HomeInteractionUiState,
    onTypeSelected: (AttendanceReportType) -> Unit,
    onReasonChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    canSubmit: Boolean = true,
    unavailableMessage: Int? = null,
) {
    val selecting = state.sheet == HomeSheet.ReportType
    Column(
        modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val title =
                if (selecting) {
                    stringResource(R.string.home_report_title)
                } else {
                    stringResource(
                        R.string.home_report_reason_title,
                        stringResource(
                            reportLabel(
                                state.reportType ?: AttendanceReportType.LATE,
                            ),
                        ),
                    )
                }
            YappText(title, YappTypography.scaleT7Bold, YappMainColor.textPrimary)
            if (selecting) {
                YappText(
                    stringResource(R.string.home_report_description),
                    YappTypography.scaleT4Medium,
                    YappMainColor.textSecondary,
                )
            }
        }
        if (selecting) {
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AttendanceReportType.entries.forEach { type ->
                    HomeReportOption(type, state.reportType == type) { onTypeSelected(type) }
                }
            }
        } else {
            YappTextField(
                value = state.reportReason,
                onValueChange = onReasonChanged,
                placeholder = stringResource(R.string.home_report_reason_hint),
                variant = YappTextFieldVariant.Home,
                enabled = !state.isSubmitting,
                textStyle = YappTypography.scaleT4Medium,
                trailingIcon = {
                    YappText(
                        stringResource(
                            R.string.home_report_reason_count,
                            state.reportReason.length,
                        ),
                        YappTypography.scaleT4Medium,
                        YappMainColor.textSubtle,
                    )
                },
            )
        }
        (unavailableMessage ?: state.errorResource)?.let { HomeSubmissionError(stringResource(it)) }
        YappButton(
            stringResource(
                if (state.isSubmitting) R.string.home_submitting else R.string.home_confirm,
            ),
            onConfirm,
            modifier = Modifier.fillMaxWidth(),
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
            enabled =
                canSubmit &&
                    !state.isSubmitting &&
                    state.reportType != null &&
                    (selecting || state.isReportReasonValid),
        )
    }
}

@Composable
internal fun HomeReportOption(
    type: AttendanceReportType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) YappMainColor.textPrimary else YappMainColor.divider
    val radioIcon =
        if (selected) {
            R.drawable.ic_home_report_radio_selected
        } else {
            R.drawable.ic_home_report_radio_default
        }

    Surface(
        color = YappMainColor.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .noRippleSelectable(
                    selected,
                    role = Role.RadioButton,
                    onClick = onClick,
                ).padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            YappText(
                stringResource(reportLabel(type)),
                YappTypography.scaleT4Medium,
                YappMainColor.textPrimary,
            )
            Image(
                painter = painterResource(radioIcon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

private fun reportLabel(type: AttendanceReportType): Int =
    when (type) {
        AttendanceReportType.LATE -> R.string.home_report_late
        AttendanceReportType.EARLY_LEAVE -> R.string.home_report_early_leave
        AttendanceReportType.ABSENT -> R.string.home_report_absent
    }
