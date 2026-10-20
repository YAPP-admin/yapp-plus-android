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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.HomeInteractionUiState
import com.yapp.plus.feature.main.home.model.HomeSheet

@Composable
internal fun HomeSheetContent(
    state: HomeInteractionUiState,
    onCodeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
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
        Box(Modifier.fillMaxWidth().height(48.dp).padding(top = 10.dp), Alignment.TopCenter) {
            state.errorResource?.let { HomeSubmissionError(stringResource(it)) }
        }
        YappButton(
            text =
                stringResource(
                    if (state.isSubmitting) R.string.home_submitting else R.string.home_confirm,
                ),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting && state.sheet == HomeSheet.AttendanceCode,
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
