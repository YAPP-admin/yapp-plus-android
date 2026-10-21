package com.yapp.plus.feature.main.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.schedule.components.ScheduleItem
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun ScheduleScreen(
    state: ScheduleUiState<List<ScheduleSessionUiModel>>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(YappMainColor.surface),
    ) {
        YappMainHeader(title = stringResource(R.string.schedule_title))
        when (state) {
            ScheduleUiState.Loading -> ScheduleLoading(modifier = Modifier.weight(1f))
            ScheduleUiState.Empty ->
                SchedulePlaceholder(
                    messageResource = R.string.schedule_empty,
                    modifier = Modifier.weight(1f),
                )

            ScheduleUiState.Error ->
                SchedulePlaceholder(
                    messageResource = R.string.schedule_error,
                    onRetry = onRetry,
                    modifier = Modifier.weight(1f),
                )

            is ScheduleUiState.Content -> {
                if (state.value.isEmpty()) {
                    SchedulePlaceholder(
                        messageResource = R.string.schedule_empty,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    ScheduleSessionsList(
                        sessions = state.value,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = YappMainColor.brand)
    }
}

@Composable
private fun SchedulePlaceholder(
    messageResource: Int,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.graphic_none),
            contentDescription = null,
            modifier = Modifier.height(74.dp),
        )
        Spacer(Modifier.height(20.dp))
        YappText(
            text = stringResource(messageResource),
            style = YappTypography.body1NormalRegular,
            color = YappMainColor.textSecondary,
            textAlign = TextAlign.Center,
        )
        onRetry?.let { retry ->
            Spacer(Modifier.height(16.dp))
            YappButton(
                text = stringResource(R.string.schedule_retry),
                onClick = retry,
                variant = YappButtonVariant.OutlinedPrimary,
                size = YappButtonSize.Medium,
            )
        }
    }
}

@Composable
private fun ScheduleSessionsList(
    sessions: List<ScheduleSessionUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        sessions.forEach { session ->
            ScheduleItem(session = session)
        }
    }
}
