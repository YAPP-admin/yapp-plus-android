package com.yapp.plus.core.designsystem.components.input

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    title: String? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    size: YappInputSize = YappInputSize.Large,
    state: YappInputState = YappInputState.Default,
    enabled: Boolean = true,
    required: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    variant: YappTextFieldVariant = YappTextFieldVariant.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle? = null,
) {
    val metrics = InputMetrics.from(size, variant)
    val isAuth = variant == YappTextFieldVariant.Auth
    val textColor = if (isAuth) YappColor.foregroundNeutral else YappColor.textPrimary
    val cursorColor = if (isAuth) YappColor.foregroundMuted else YappColor.primary
    val placeholderColor = if (isAuth) YappColor.foregroundFaint else YappColor.placeholder
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shouldHighlightFocus =
        isAuth &&
            isFocused &&
            enabled &&
            state == YappInputState.Default
    val borderState = if (shouldHighlightFocus) YappInputState.Active else state
    val borderColor = inputBorderColor(borderState, variant)
    val inputShape = RoundedCornerShape(metrics.cornerRadius)
    val currentSelectionColors = LocalTextSelectionColors.current
    val textSelectionColors =
        remember(currentSelectionColors) {
            TextSelectionColors(
                handleColor = YappColor.black,
                backgroundColor = currentSelectionColors.backgroundColor,
            )
        }
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null) {
                YappText(
                    title,
                    style = YappTypography.label1NormalMedium,
                    color = YappColor.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
            }
            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(metrics.height)
                            .border(width = 1.dp, color = borderColor, shape = inputShape)
                            .padding(horizontal = metrics.horizontalPadding),
                    enabled = enabled,
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    interactionSource = interactionSource,
                    textStyle = (textStyle ?: inputTextStyle(size, variant)).copy(color = textColor),
                    cursorBrush = SolidColor(cursorColor),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(metrics.iconSpacing),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            leadingIcon?.invoke()
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (value.isEmpty()) {
                                    YappText(
                                        placeholder,
                                        style = inputTextStyle(size, variant),
                                        color = placeholderColor,
                                    )
                                }
                                innerTextField()
                            }
                            trailingIcon?.invoke()
                        }
                    },
                )
            }
            if (supportingText != null) {
                Spacer(Modifier.height(8.dp))
                YappText(
                    text = supportingText,
                    style = YappTypography.label2Regular,
                    color = supportingTextColor(state),
                )
            }
        }
        if (required) RequiredBadge()
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappTextFieldPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = YappColor.white) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                YappTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "이름을 입력해주세요",
                    title = "이름",
                    state = YappInputState.Active,
                    supportingText = "텍스트를 입력해주세요",
                    required = true,
                )
                YappTextField(
                    value = "잘못된 입력",
                    onValueChange = {},
                    placeholder = "이메일을 입력해주세요",
                    title = "이메일",
                    size = YappInputSize.Medium,
                    state = YappInputState.Error,
                    supportingText = "이메일 형식으로 입력해주세요",
                )
            }
        }
    }
}
