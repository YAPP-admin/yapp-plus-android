package com.yapp.plus.feature.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun DemoDataLabel(modifier: Modifier = Modifier) {
    YappText(
        text = stringResource(R.string.attendance_demo_data),
        style = YappTypography.caption1Bold,
        color = YappMainColor.textSecondary,
        modifier = modifier,
    )
}
