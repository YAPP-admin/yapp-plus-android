package com.yapp.plus.feature.main.home.team.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography

@Composable
internal fun HomeTeamSessionError(
    @StringRes messageResource: Int,
) {
    YappText(
        stringResource(messageResource),
        YappHomeTypography.body,
        YappHomeColor.muted,
    )
}
