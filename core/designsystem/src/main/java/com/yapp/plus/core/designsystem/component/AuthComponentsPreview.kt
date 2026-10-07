package com.yapp.plus.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.component.button.YappButton
import com.yapp.plus.core.designsystem.component.button.YappButtonSize
import com.yapp.plus.core.designsystem.component.button.YappButtonVariant
import com.yapp.plus.core.designsystem.component.input.YappInputState
import com.yapp.plus.core.designsystem.component.input.YappTextField
import com.yapp.plus.core.designsystem.component.input.YappTextFieldVariant
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialog
import com.yapp.plus.core.designsystem.component.popup.YappAlertDialogVariant
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTheme

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AuthComponentsPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = YappColor.white) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                for (isEnabled in listOf(true, false)) {
                    YappButton(
                        text = stringResource(R.string.yapp_preview_auth_next),
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        variant = YappButtonVariant.SolidBrand,
                        size = YappButtonSize.CallToAction,
                        enabled = isEnabled,
                    )
                }
                for (state in listOf(YappInputState.Default, YappInputState.Active)) {
                    YappTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.padding(horizontal = 4.dp),
                        placeholder = stringResource(R.string.yapp_preview_auth_name_hint),
                        variant = YappTextFieldVariant.Auth,
                        state = state,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CompactAlertPreview() {
    YappTheme(dynamicColor = false) {
        YappAlertDialog(
            onDismissRequest = {},
            title = stringResource(R.string.yapp_preview_auth_error_title),
            body = stringResource(R.string.yapp_preview_auth_error_body),
            confirmText = stringResource(R.string.yapp_preview_auth_report),
            dismissText = stringResource(R.string.yapp_preview_auth_close),
            onConfirm = {},
            onDismiss = {},
            variant = YappAlertDialogVariant.Compact,
        )
    }
}
