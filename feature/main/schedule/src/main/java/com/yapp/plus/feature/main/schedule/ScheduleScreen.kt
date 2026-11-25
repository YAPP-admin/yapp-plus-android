package com.yapp.plus.feature.main.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    targetSessionId: Long? = null,
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
            ScheduleUiState.Inactive ->
                SchedulePlaceholder(
                    messageResource = R.string.session_inactive,
                    modifier = Modifier.weight(1f),
                )
            ScheduleUiState.Empty ->
                SchedulePlaceholder(
                    messageResource = R.string.schedule_empty,
                    modifier = Modifier.weight(1f),
                )

            ScheduleUiState.Error ->
                SchedulePlaceholder(
                    messageResource = R.string.schedule_error,
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
                        targetSessionId = targetSessionId,
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
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = YappMainColor.brand)
    }
}

@Composable
private fun SchedulePlaceholder(
    messageResource: Int,
    modifier: Modifier = Modifier,
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
    }
}

@Composable
private fun ScheduleSessionsList(
    sessions: List<ScheduleSessionUiModel>,
    targetSessionId: Long?,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    var hasShownTarget by rememberSaveable(targetSessionId) { mutableStateOf(false) }
    LaunchedEffect(targetSessionId, sessions) {
        if (targetSessionId == null || hasShownTarget) return@LaunchedEffect
        val index = sessions.indexOfFirst { it.sessionId == targetSessionId }
        if (index >= 0) {
            listState.scrollToItem(index)
            hasShownTarget = true
        }
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
    ) {
        items(sessions) { session -> ScheduleItem(session = session) }
    }
}
