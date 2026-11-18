package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.feature.main.home.R
import com.yapp.plus.feature.main.home.model.SessionDistanceUiState

@Composable
internal fun HomeSessionDistance(
    state: SessionDistanceUiState,
    onMeasure: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val result = (state as? SessionDistanceUiState.Result)?.distance
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (result != null) {
            YappText(
                text = distanceMessage(result),
                style = YappHomeTypography.label,
                color = YappHomeColor.subtle,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        val buttonText =
            when {
                state == SessionDistanceUiState.Loading -> R.string.home_distance_loading
                result is SessionDistance.Measured -> R.string.home_distance_refresh
                result == SessionDistance.Unavailable ||
                    result == SessionDistance.LocationDisabled -> R.string.attendance_retry
                else -> R.string.home_distance_check
            }
        YappButton(
            text = stringResource(buttonText),
            onClick = onMeasure,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            variant = YappButtonVariant.OutlinedAssistive,
            size = YappButtonSize.HomeSecondary,
            enabled = state != SessionDistanceUiState.Loading,
        )
        if (result == SessionDistance.PermissionRequired ||
            result == SessionDistance.LocationDisabled
        ) {
            YappButton(
                text = stringResource(R.string.home_distance_open_settings),
                onClick = onOpenSettings,
                modifier = Modifier.heightIn(min = 48.dp),
                variant = YappButtonVariant.OutlinedAssistive,
                size = YappButtonSize.HomeSecondary,
            )
        }
    }
}

@Composable
private fun distanceMessage(result: SessionDistance): String =
    when (result) {
        is SessionDistance.Measured -> {
            val distance = stringResource(R.string.home_distance_result, result.meters)
            if (result.isApproximate) {
                "$distance\n${stringResource(R.string.home_distance_approximate)}"
            } else {
                distance
            }
        }
        SessionDistance.PermissionRequired -> stringResource(R.string.home_distance_permission)
        SessionDistance.LocationDisabled -> stringResource(R.string.home_distance_disabled)
        SessionDistance.Unavailable -> stringResource(R.string.home_distance_unavailable)
    }
