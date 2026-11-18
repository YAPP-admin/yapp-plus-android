package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.domain.session.SessionDistance
import com.yapp.plus.feature.main.home.model.SessionDistanceUiState

@Preview(widthDp = 300)
@Composable
private fun HomeSessionDistancePreview() {
    YappTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(
                SessionDistanceUiState.Idle,
                SessionDistanceUiState.Result(
                    SessionDistance.Measured(meters = 182, isApproximate = true),
                ),
                SessionDistanceUiState.Result(SessionDistance.PermissionRequired),
            ).forEach {
                HomeSessionDistance(state = it, onMeasure = {}, onOpenSettings = {})
            }
        }
    }
}
