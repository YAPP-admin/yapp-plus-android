package com.yapp.plus.feature.main.my.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.my.R

@Composable
internal fun MyRequestState(
    message: String,
    isLoading: Boolean = false,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = YappMainColor.brand,
                strokeWidth = 2.dp,
            )
        }
        YappText(message, YappTypography.scaleT4Medium, YappMainColor.textSecondary)
        onRetry?.let {
            TextButton(onClick = it) {
                YappText(
                    stringResource(R.string.my_retry),
                    YappTypography.scaleT4Bold,
                    YappMainColor.brand,
                )
            }
        }
    }
}
