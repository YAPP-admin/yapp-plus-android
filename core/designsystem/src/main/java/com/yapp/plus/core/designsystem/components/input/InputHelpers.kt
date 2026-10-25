package com.yapp.plus.core.designsystem.components.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
internal fun RequiredBadge() {
    Image(
        painter = painterResource(R.drawable.ic_input_required),
        contentDescription = null,
        modifier = Modifier.offset(x = (-8).dp, y = (-8).dp).size(16.dp),
    )
}

internal fun inputTextStyle(
    size: YappInputSize,
    variant: YappTextFieldVariant = YappTextFieldVariant.Default,
): TextStyle =
    if (variant == YappTextFieldVariant.Auth) {
        YappTypography.scaleT5Regular
    } else {
        when (size) {
            YappInputSize.Large -> YappTypography.body1NormalRegular
            YappInputSize.Medium -> YappTypography.body2NormalRegular
            YappInputSize.Small -> YappTypography.label2Regular
        }
    }

internal fun inputBorderColor(
    state: YappInputState,
    variant: YappTextFieldVariant = YappTextFieldVariant.Default,
) = when {
    state == YappInputState.Error -> YappColor.error
    variant == YappTextFieldVariant.Auth && state == YappInputState.Active -> {
        YappColor.foregroundMuted
    }
    variant == YappTextFieldVariant.Auth -> YappColor.strokeNeutralWeak
    state == YappInputState.Active -> YappColor.primary
    state == YappInputState.Success -> YappColor.borderStrong
    else -> YappColor.border
}

internal fun supportingTextColor(state: YappInputState) =
    when (state) {
        YappInputState.Success -> YappColor.success
        YappInputState.Error -> YappColor.error
        YappInputState.Default,
        YappInputState.Active,
        -> YappColor.textPrimary
    }
