package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.toast.YappToast
import com.yapp.plus.core.designsystem.components.toast.YappToastVariant
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeReportConfirmation(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    YappToast(
        text = stringResource(R.string.home_report_complete),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        variant = YappToastVariant.Snackbar,
        showIcon = false,
        actionText = stringResource(R.string.home_confirm),
        onAction = onDismiss,
    )
}
