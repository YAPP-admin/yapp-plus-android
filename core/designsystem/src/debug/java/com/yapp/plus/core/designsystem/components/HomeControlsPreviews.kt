package com.yapp.plus.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.button.YappButtonVariant
import com.yapp.plus.core.designsystem.components.input.YappTextField
import com.yapp.plus.core.designsystem.components.input.YappTextFieldVariant
import com.yapp.plus.core.designsystem.components.main.YappMainHeader
import com.yapp.plus.core.designsystem.components.popup.YappBottomSheetHandle
import com.yapp.plus.core.designsystem.components.toast.YappToast
import com.yapp.plus.core.designsystem.components.toast.YappToastVariant
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(widthDp = 393)
@Composable
private fun HomeHeaderVariantPreview() {
    YappTheme {
        YappMainHeader(
            title = stringResource(R.string.yapp_preview_home_title),
            iconResource = R.drawable.ic_notification,
            horizontalPadding = 16.dp,
            iconTint = YappHomeColor.subtle,
            leadingContent = { Image(painterResource(R.drawable.graphic_yapp_logo), null) },
        )
    }
}

@Preview(widthDp = 361)
@Composable
private fun HomeButtonVariantsPreview() {
    YappTheme {
        Column {
            YappButton(stringResource(R.string.yapp_preview_home_confirm), {
            }, variant = YappButtonVariant.SolidBrand, size = YappButtonSize.HomePrimary)
            YappButton(stringResource(R.string.yapp_preview_home_check_in), {
            }, variant = YappButtonVariant.SolidNeutral, size = YappButtonSize.HomeSecondary)
            YappButton(
                stringResource(R.string.yapp_preview_home_attended),
                {},
                variant = YappButtonVariant.SolidNeutral,
                size = YappButtonSize.HomeSecondary,
                enabled = false,
            )
        }
    }
}

@Preview(widthDp = 373)
@Composable
private fun HomeSheetHandlePreview() {
    YappTheme { YappBottomSheetHandle(contentDescription = null, isHome = true) }
}

@Preview(widthDp = 361)
@Composable
private fun HomeActionToastPreview() {
    YappTheme {
        YappToast(
            text = stringResource(R.string.yapp_preview_home_report_complete),
            variant = YappToastVariant.Home,
            modifier = Modifier.fillMaxWidth(),
            showIcon = false,
            actionText = stringResource(R.string.yapp_preview_home_confirm),
        )
    }
}

@Preview(widthDp = 373)
@Composable
private fun HomeReasonInputVariantPreview() {
    YappTheme {
        YappTextField(
            value = "",
            onValueChange = {},
            title = null,
            placeholder = stringResource(R.string.yapp_preview_home_reason_hint),
            modifier = Modifier.padding(18.dp),
            variant = YappTextFieldVariant.Auth,
            textStyle = YappHomeTypography.input,
        )
    }
}
