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
import com.yapp.plus.core.designsystem.component.button.YappButton
import com.yapp.plus.core.designsystem.component.button.YappButtonSize
import com.yapp.plus.core.designsystem.component.button.YappButtonVariant
import com.yapp.plus.core.designsystem.component.main.YappDemoDataLabel
import com.yapp.plus.core.designsystem.component.main.YappMainHeader
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.schedule.component.ScheduleItem
import com.yapp.plus.feature.main.schedule.model.ScheduleSessionUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleStatusUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleTypeUiModel
import com.yapp.plus.feature.main.schedule.model.ScheduleUiState
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
fun ScheduleScreen(modifier: Modifier = Modifier) {
    ScheduleScreen(
        state = demoScheduleUiState(),
        modifier = modifier,
        showDemoDataLabel = true,
    )
}

@Composable
fun ScheduleScreen(
    state: ScheduleUiState<List<ScheduleSessionUiModel>>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    showDemoDataLabel: Boolean = false,
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
                        showDemoDataLabel = showDemoDataLabel,
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
    showDemoDataLabel: Boolean,
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
        if (showDemoDataLabel) {
            YappDemoDataLabel(
                text = stringResource(R.string.schedule_demo_data),
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun demoScheduleUiState(): ScheduleUiState<List<ScheduleSessionUiModel>> =
    ScheduleUiState.Content(
        listOf(
            demoScheduleSession(
                dateResource = R.string.schedule_date_first,
                nameResource = R.string.schedule_team,
                locationResource = null,
                timeResource = R.string.schedule_time,
                type = ScheduleTypeUiModel.Team,
                status = ScheduleStatusUiModel.Past,
            ),
            demoScheduleSession(
                dateResource = R.string.schedule_date_second,
                nameResource = R.string.schedule_planning,
                locationResource = R.string.schedule_place_second,
                timeResource = R.string.schedule_time_second,
                type = ScheduleTypeUiModel.Regular,
                status = ScheduleStatusUiModel.Past,
            ),
            demoScheduleSession(
                dateResource = R.string.schedule_date_third,
                nameResource = R.string.schedule_team,
                locationResource = null,
                timeResource = R.string.schedule_time,
                type = ScheduleTypeUiModel.Team,
                status = ScheduleStatusUiModel.Today,
            ),
            demoScheduleSession(
                dateResource = R.string.schedule_date_fourth,
                nameResource = R.string.schedule_team,
                locationResource = null,
                timeResource = R.string.schedule_time,
                type = ScheduleTypeUiModel.Team,
                status = ScheduleStatusUiModel.Upcoming,
            ),
            demoScheduleSession(
                dateResource = R.string.schedule_date_fifth,
                nameResource = R.string.schedule_user_test,
                locationResource = R.string.schedule_place_fifth,
                timeResource = R.string.schedule_time_fifth,
                type = ScheduleTypeUiModel.Regular,
                status = ScheduleStatusUiModel.Upcoming,
            ),
            demoScheduleSession(
                dateResource = R.string.schedule_date_sixth,
                nameResource = R.string.schedule_team,
                locationResource = null,
                timeResource = R.string.schedule_time,
                type = ScheduleTypeUiModel.Team,
                status = ScheduleStatusUiModel.Upcoming,
            ),
        ),
    )

@Composable
private fun demoScheduleSession(
    dateResource: Int,
    nameResource: Int,
    locationResource: Int?,
    timeResource: Int,
    type: ScheduleTypeUiModel,
    status: ScheduleStatusUiModel,
) = ScheduleSessionUiModel(
    dateLabel = stringResource(dateResource),
    name = stringResource(nameResource),
    location = locationResource?.let { resource -> stringResource(resource) },
    timeLabel = stringResource(timeResource),
    type = type,
    status = status,
)
