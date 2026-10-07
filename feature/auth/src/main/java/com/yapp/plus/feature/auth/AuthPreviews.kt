package com.yapp.plus.feature.auth

import androidx.compose.foundation.layout.Box
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

@Preview(name = "Kakao login", widthDp = 393, heightDp = 756)
@Preview(name = "Compact login", widthDp = 320, heightDp = 480)
@Composable
private fun LoginPreview() {
    AuthPreview {
        LoginContent(onBack = {}, onKakaoLogin = {})
    }
}

@Preview(name = "Empty input", widthDp = 393, heightDp = 756)
@Composable
private fun EmptySignUpPreview(
    @PreviewParameter(SignUpStepPreviewProvider::class) step: SignUpStep,
) {
    SignUpPreview(step = step, value = "", inputState = YappInputState.Default)
}

@Preview(name = "Focused input", widthDp = 393, heightDp = 756)
@Composable
private fun FocusedSignUpPreview(
    @PreviewParameter(SignUpStepPreviewProvider::class) step: SignUpStep,
) {
    SignUpPreview(step = step, value = "", inputState = YappInputState.Active)
}

@Preview(name = "Entered input", widthDp = 393, heightDp = 756)
@Composable
private fun EnteredSignUpPreview(
    @PreviewParameter(SignUpStepPreviewProvider::class) step: SignUpStep,
) {
    val resource =
        if (step == SignUpStep.Name) R.string.auth_preview_name else R.string.auth_preview_phone
    SignUpPreview(
        step = step,
        value = stringResource(resource),
        inputState = YappInputState.Default,
    )
}

@Composable
private fun SignUpPreview(
    step: SignUpStep,
    value: String,
    inputState: YappInputState,
) {
    var previewValue by remember(step, value) { mutableStateOf(value) }
    AuthPreview {
        SignUpContent(
            step = step,
            value = previewValue,
            onValueChange = { previewValue = it },
            isNextEnabled = previewValue.isNotEmpty(),
            onNext = {},
            onBack = {},
            inputState = inputState,
        )
    }
}

@Preview(name = "Auth status", widthDp = 393, heightDp = 756)
@Composable
private fun StatusPreview(
    @PreviewParameter(AuthStatusPreviewProvider::class) status: AuthStatus,
) {
    AuthPreview {
        AuthStatusContent(status = status, onBack = {}, onAction = {})
    }
}

@Preview(name = "Auth error", widthDp = 393, heightDp = 756)
@Composable
private fun ErrorPreview() {
    AuthPreview {
        LoginContent(onBack = {}, onKakaoLogin = {})
        AuthErrorDialog(onDismiss = {}, onReport = {})
    }
}

@Composable
private fun AuthPreview(content: @Composable () -> Unit) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(containerColor = YappAuthColor.background) { padding ->
            Box(
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding),
            ) {
                content()
            }
        }
    }
}
