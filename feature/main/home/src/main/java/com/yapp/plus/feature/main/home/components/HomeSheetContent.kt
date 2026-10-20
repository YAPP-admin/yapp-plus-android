package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.input.YappTextField
import com.yapp.plus.core.designsystem.components.input.YappTextFieldVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.domain.attendance.AttendanceReportType
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet
import com.yapp.plus.feature.main.home.model.ReportChoice

@Composable
internal fun HomeSheetContent(
    state: HomeInteractionUiState,
    onCodeChange: (String) -> Unit,
    onReasonChange: (String) -> Unit,
    onReportChoice: (ReportChoice) -> Unit,
    onLateDuration: (AttendanceReportType) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        HomeSheetHeading(state)
        Spacer(Modifier.height(if (state.sheet == HomeSheet.AttendanceCode) 24.dp else 20.dp))
        when (state.sheet) {
            HomeSheet.AttendanceCode ->
                HomeCodeInput(
                    value = state.code,
                    onValueChange = onCodeChange,
                    isError = state.isInvalidCode,
                    enabled = !state.isSubmitting,
                    onConfirm = onConfirm,
                )
            HomeSheet.ReportKind -> ReportChoices(state, onReportChoice)
            HomeSheet.LateDuration -> LateDurationChoices(state, onLateDuration)
            HomeSheet.ReportReason ->
                YappTextField(
                    value = state.reason,
                    onValueChange = onReasonChange,
                    title = null,
                    placeholder = stringResource(R.string.home_reason_placeholder),
                    variant = YappTextFieldVariant.Auth,
                    enabled = !state.isSubmitting,
                    textStyle = YappHomeTypography.input,
                    trailingIcon = {
                        YappText(
                            stringResource(R.string.home_reason_count, state.reason.length),
                            YappHomeTypography.body,
                            YappHomeColor.subtle,
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                )
            HomeSheet.None -> Unit
        }
        if (state.sheet == HomeSheet.AttendanceCode) {
            Box(Modifier.fillMaxWidth().height(48.dp).padding(top = 10.dp), Alignment.TopCenter) {
                state.errorResource?.let { HomeSubmissionError(stringResource(it)) }
            }
        } else {
            state.errorResource?.let {
                Spacer(Modifier.height(10.dp))
                HomeSubmissionError(stringResource(it))
            }
            Spacer(Modifier.height(20.dp))
        }
        YappButton(
            text =
                stringResource(
                    if (state.isSubmitting) R.string.home_submitting else R.string.home_confirm,
                ),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = canConfirm(state),
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
        )
    }
}

@Composable
internal fun HomeSheetHeading(state: HomeInteractionUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val title =
            when (state.sheet) {
                HomeSheet.AttendanceCode -> R.string.home_code_title
                HomeSheet.ReportKind -> R.string.home_report_title
                HomeSheet.LateDuration -> R.string.home_late_duration_title
                else ->
                    when (state.reportChoice) {
                        ReportChoice.Late -> R.string.home_reason_late
                        ReportChoice.EarlyLeave -> R.string.home_reason_early_leave
                        else -> R.string.home_reason_absent
                    }
            }
        YappText(stringResource(title), YappHomeTypography.title, YappHomeColor.text)
        val description =
            when (state.sheet) {
                HomeSheet.AttendanceCode -> R.string.home_code_description
                HomeSheet.ReportKind -> R.string.home_report_description
                else -> null
            }
        description?.let {
            YappText(stringResource(it), YappHomeTypography.body, YappHomeColor.muted)
        }
    }
}

@Composable
internal fun ReportChoices(
    state: HomeInteractionUiState,
    onSelect: (ReportChoice) -> Unit,
) {
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (choice in ReportChoice.entries) {
            val label =
                when (choice) {
                    ReportChoice.Late -> R.string.home_report_late
                    ReportChoice.EarlyLeave -> R.string.home_report_early_leave
                    ReportChoice.Absent -> R.string.home_report_absent
                }
            HomeReportOption(
                label = stringResource(label),
                isSelected = state.reportChoice == choice,
                enabled = !state.isSubmitting,
                onClick = { onSelect(choice) },
            )
        }
    }
}

@Composable
internal fun LateDurationChoices(
    state: HomeInteractionUiState,
    onSelect: (AttendanceReportType) -> Unit,
) {
    val options =
        listOf(
            AttendanceReportType.ONE_HOUR_LATE to R.string.home_late_one_hour,
            AttendanceReportType.THREE_HOUR_LATE to R.string.home_late_three_hours,
        )
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for ((type, label) in options) {
            HomeReportOption(
                label = stringResource(label),
                isSelected = state.reportType == type,
                enabled = !state.isSubmitting,
                onClick = { onSelect(type) },
            )
        }
    }
}

@Composable
internal fun HomeSubmissionError(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(painterResource(R.drawable.home_input_error), null, Modifier.size(16.dp))
        YappText(message, YappHomeTypography.label, YappHomeColor.error)
    }
}

private fun canConfirm(state: HomeInteractionUiState): Boolean =
    !state.isSubmitting &&
        when (state.sheet) {
            HomeSheet.AttendanceCode -> true
            HomeSheet.ReportKind -> state.reportChoice != null
            HomeSheet.LateDuration -> state.reportType != null
            HomeSheet.ReportReason -> state.reason.isNotBlank()
            HomeSheet.None -> false
        }
