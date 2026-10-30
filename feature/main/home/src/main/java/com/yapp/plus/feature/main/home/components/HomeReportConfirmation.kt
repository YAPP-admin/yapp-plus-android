package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeReportConfirmation(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier, color = YappHomeColor.text, shape = RoundedCornerShape(8.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            YappText(
                stringResource(R.string.home_report_complete),
                YappTypography.scaleT3Medium,
                YappMainColor.surface,
            )
            YappText(
                stringResource(R.string.home_confirm),
                YappTypography.scaleT3Medium,
                YappMainColor.brand,
                Modifier.clickable(onClick = onDismiss),
            )
        }
    }
}
