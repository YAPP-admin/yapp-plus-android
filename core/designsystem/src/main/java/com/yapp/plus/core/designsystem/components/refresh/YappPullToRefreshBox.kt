package com.yapp.plus.core.designsystem.components.refresh

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yapp.plus.core.designsystem.theme.YappMainColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YappPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    showIndicator: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier,
        indicator = {
            YappPullToRefreshIndicator(
                state = state,
                isRefreshing = isRefreshing,
                showIndicator = showIndicator,
            )
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.YappPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    showIndicator: Boolean,
) {
    if (!showIndicator) return
    PullToRefreshDefaults.Indicator(
        state = state,
        isRefreshing = isRefreshing,
        modifier = Modifier.align(Alignment.TopCenter),
        color = YappMainColor.brand,
        containerColor = YappMainColor.surface,
    )
}
