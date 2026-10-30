package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.R

@Composable
internal fun MyAttendanceRulesDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { window?.setDimAmount(0.46f) }
        Surface(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .widthIn(max = 346.dp),
            shape = RoundedCornerShape(16.dp),
            color = YappMainColor.surface,
        ) {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                YappText(
                    stringResource(R.string.my_attendance_rules),
                    YappTypography.scaleT6Bold,
                    YappMainColor.textPrimary,
                )
                Spacer(Modifier.height(12.dp))
                AttendanceRuleGroup(
                    R.string.my_rule_one_hour,
                    R.string.my_rule_four,
                    R.string.my_rule_ten,
                )
                Spacer(Modifier.height(14.dp))
                AttendanceRuleGroup(
                    R.string.my_rule_three_hours,
                    R.string.my_rule_eight,
                    R.string.my_rule_fourteen,
                )
                Spacer(Modifier.height(14.dp))
                AttendanceRuleGroup(
                    R.string.my_status_absent,
                    R.string.my_rule_twelve,
                    R.string.my_rule_twenty,
                )
                Spacer(Modifier.height(14.dp))
                AttendanceRulesNotice()
                Spacer(Modifier.height(16.dp))
                YappButton(
                    text = stringResource(R.string.my_confirm),
                    onClick = onDismiss,
                    variant = YappButtonVariant.SolidBrand,
                    size = YappButtonSize.HomeSecondary,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                )
            }
        }
    }
}

@Composable
internal fun AttendanceRulesNotice() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(YappMainColor.layerFill, RoundedCornerShape(8.dp))
                .padding(start = 8.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
    ) {
        listOf(R.string.my_rule_dismissal_notice, R.string.my_rule_reporting_notice).forEach {
            Row {
                YappText(
                    text = "•",
                    style = YappTypography.scaleT3Medium,
                    color = YappMainColor.textSecondary,
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center,
                )
                YappText(
                    text = stringResource(it),
                    style = YappTypography.scaleT3Medium,
                    color = YappMainColor.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
internal fun AttendanceRuleGroup(
    title: Int,
    reported: Int,
    unreported: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AttendanceRuleRow(stringResource(title), stringResource(reported))
        AttendanceRuleRow(
            label =
                stringResource(
                    if (title == R.string.my_status_absent) {
                        R.string.my_rule_unreported_absence
                    } else {
                        R.string.my_rule_unreported_late
                    },
                ),
            deduction = stringResource(unreported),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun AttendanceRuleRow(
    label: String,
    deduction: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        YappText(label, YappTypography.scaleT4Medium, YappMainColor.textSecondary)
        Canvas(
            Modifier
                .weight(1f)
                .height(1.dp),
        ) {
            drawLine(
                color = YappMainColor.divider,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx())),
            )
        }
        YappText(deduction, YappTypography.scaleT4Bold, YappMainColor.brand)
    }
}
