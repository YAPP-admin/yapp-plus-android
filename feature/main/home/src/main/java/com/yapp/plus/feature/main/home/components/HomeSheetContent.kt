package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.attendance.components.HomeCodeInput
import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet

@Composable
internal fun HomeSheetContent(
    state: HomeInteractionUiState,
    onCodeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isCheckingLocation: Boolean = false,
    locationIssue: HomeAttendanceLocationIssue? = null,
    onLocationRecovery: (HomeAttendanceLocationIssue) -> Unit = {},
) {
    Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        HomeSheetHeading()
        Spacer(Modifier.height(24.dp))
        HomeCodeInput(
            value = state.code,
            onValueChange = onCodeChange,
            isError = state.isInvalidCode,
            enabled = !state.isSubmitting,
            onConfirm = onConfirm,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(vertical = 10.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val issue = locationIssue
            (issue?.messageResource ?: state.errorResource)?.let {
                HomeSubmissionError(stringResource(it))
            }
            val actionRes = issue?.actionResource
            if (actionRes != null) {
                YappButton(
                    text = stringResource(actionRes),
                    onClick = { onLocationRecovery(issue) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = YappButtonVariant.OutlinedAssistive,
                    size = YappButtonSize.HomeSecondary,
                )
            }
        }
        YappButton(
            text =
                stringResource(
                    when {
                        isCheckingLocation -> R.string.home_distance_loading
                        state.isSubmitting -> R.string.home_submitting
                        else -> R.string.home_confirm
                    },
                ),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            enabled =
                state.code.length == 4 &&
                    !state.isSubmitting &&
                    state.sheet == HomeSheet.AttendanceCode,
            variant = YappButtonVariant.SolidBrand,
            size = YappButtonSize.HomePrimary,
        )
    }
}

@Composable
internal fun HomeSheetHeading() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        YappText(
            stringResource(R.string.home_code_title),
            YappHomeTypography.title,
            YappHomeColor.text,
        )
        YappText(
            stringResource(R.string.home_code_description),
            YappHomeTypography.body,
            YappHomeColor.muted,
        )
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
