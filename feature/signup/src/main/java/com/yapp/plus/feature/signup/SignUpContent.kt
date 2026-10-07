package com.yapp.plus.feature.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.component.auth.AuthHeading
import com.yapp.plus.core.designsystem.component.auth.AuthLayout
import com.yapp.plus.core.designsystem.component.auth.AuthPrimaryAction
import com.yapp.plus.core.designsystem.component.input.YappInputState
import com.yapp.plus.core.designsystem.component.input.YappTextField
import com.yapp.plus.core.designsystem.component.input.YappTextFieldVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignUpContent(
    step: SignUpStep,
    value: String,
    onValueChange: (String) -> Unit,
    isNextEnabled: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    inputState: YappInputState = YappInputState.Default,
    isInputEnabled: Boolean = true,
) {
    val isName = step == SignUpStep.Name
    val title = if (isName) R.string.auth_name_title else R.string.auth_phone_title
    val hint = if (isName) R.string.auth_name_hint else R.string.auth_phone_hint
    val focusManager = LocalFocusManager.current
    var hasInputFocus by remember(step) { mutableStateOf(false) }
    val isImeVisible = WindowInsets.isImeVisible
    AuthLayout(
        onBack = onBack,
        modifier = modifier.imePadding(),
        bottom = {
            if (!isImeVisible) {
                AuthPrimaryAction(
                    text = stringResource(R.string.auth_next),
                    onClick = onNext,
                    isEnabled = isNextEnabled,
                )
            }
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            AuthHeading(stringResource(title))
            Spacer(Modifier.height(40.dp))
            YappTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.onFocusChanged { hasInputFocus = it.hasFocus },
                placeholder =
                    if (hasInputFocus || inputState == YappInputState.Active) {
                        ""
                    } else {
                        stringResource(hint)
                    },
                state = inputState,
                enabled = isInputEnabled,
                variant = YappTextFieldVariant.Auth,
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = if (isName) KeyboardType.Text else KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            )
        }
    }
}
