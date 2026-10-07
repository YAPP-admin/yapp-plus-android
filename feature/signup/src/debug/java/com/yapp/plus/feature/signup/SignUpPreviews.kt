package com.yapp.plus.feature.signup

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.yapp.plus.core.designsystem.component.input.YappInputState
import com.yapp.plus.core.designsystem.theme.YappAuthColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.preview.TextInputPreviewFixture

@Preview(name = "Signup input", widthDp = 393, heightDp = 756)
@Composable
private fun SignUpContentPreview(
    @PreviewParameter(SignUpInputPreviewProvider::class)
    preview: Pair<SignUpStep, TextInputPreviewFixture>,
) {
    val (step, fixture) = preview
    val resource =
        if (step == SignUpStep.Name) R.string.auth_preview_name else R.string.auth_preview_phone
    val value = if (fixture.value.isEmpty()) "" else stringResource(resource)
    var previewValue by remember(step, fixture) { mutableStateOf(value) }
    val inputState =
        when {
            fixture.hasError -> YappInputState.Error
            fixture.isFocused -> YappInputState.Active
            else -> YappInputState.Default
        }
    YappTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(containerColor = YappAuthColor.background) { padding ->
            SignUpContent(
                step = step,
                value = previewValue,
                onValueChange = { previewValue = it },
                isNextEnabled = previewValue.isNotEmpty(),
                onNext = {},
                onBack = {},
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
                inputState = inputState,
                isInputEnabled = fixture.isEnabled,
            )
        }
    }
}

@Preview(name = "Signup status", widthDp = 393, heightDp = 756)
@Composable
private fun AuthStatusContentPreview(
    @PreviewParameter(AuthStatusPreviewProvider::class) status: AuthStatus,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(containerColor = YappAuthColor.background) { padding ->
            AuthStatusContent(
                status = status,
                onBack = {},
                onAction = {},
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
            )
        }
    }
}

@Preview(name = "Signup status action", showBackground = true, widthDp = 393, heightDp = 90)
@Composable
private fun AuthStatusActionPreview(
    @PreviewParameter(AuthStatusPreviewProvider::class) status: AuthStatus,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        AuthStatusAction(status = status, onAction = {}, isEnabled = true)
    }
}
