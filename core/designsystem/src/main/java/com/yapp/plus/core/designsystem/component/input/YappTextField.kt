package com.yapp.plus.core.designsystem.component.input

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "내용을 입력해주세요",
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
) {
    val metrics = inputMetrics(size, variant)
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
                textStyle = inputTextStyle(size, variant).copy(color = textColor),
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

@Composable
fun YappTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    placeholder: String = "내용을 입력해주세요",
    size: YappTextAreaSize = YappTextAreaSize.Large,
    state: YappInputState = YappInputState.Default,
    maxLength: Int = 1000,
    enabled: Boolean = true,
    required: Boolean = false,
) {
    val inputSize =
        if (size == YappTextAreaSize.Large) {
            YappInputSize.Large
        } else {
            YappInputSize.Medium
        }
    val metrics = inputMetrics(inputSize)
    val borderColor = inputBorderColor(state)
    val inputShape = RoundedCornerShape(metrics.cornerRadius)
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            YappText(
                title,
                style = YappTypography.label1NormalMedium,
                color = YappColor.textPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(width = 1.dp, color = borderColor, shape = inputShape)
                        .padding(horizontal = metrics.horizontalPadding, vertical = 12.dp),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { if (it.length <= maxLength) onValueChange(it) },
                    modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
                    enabled = enabled,
                    textStyle = inputTextStyle(inputSize).copy(color = YappColor.textPrimary),
                    cursorBrush = SolidColor(YappColor.primary),
                    decorationBox = { innerTextField ->
                        Box(Modifier.fillMaxSize()) {
                            if (value.isEmpty()) {
                                YappText(
                                    placeholder,
                                    style = inputTextStyle(inputSize),
                                    color = YappColor.placeholder,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
                YappText(
                    text = "${value.length} / $maxLength",
                    modifier = Modifier.align(Alignment.BottomEnd),
                    style = YappTypography.label2Regular,
                    color = YappColor.neutralText,
                )
            }
        }
        if (required) RequiredBadge()
    }
}

@Composable
private fun RequiredBadge() {
    Image(
        painter = painterResource(R.drawable.ic_input_required),
        contentDescription = null,
        modifier = Modifier.offset(x = (-8).dp, y = (-8).dp).size(16.dp),
    )
}

private fun inputTextStyle(
    size: YappInputSize,
    variant: YappTextFieldVariant = YappTextFieldVariant.Default,
): TextStyle =
    if (variant == YappTextFieldVariant.Auth) {
        YappTypography.body1NormalRegular.copy(letterSpacing = 0.sp)
    } else {
        when (size) {
            YappInputSize.Large -> YappTypography.body1NormalRegular
            YappInputSize.Medium -> YappTypography.body2NormalRegular
            YappInputSize.Small -> YappTypography.label2Regular
        }
    }

private fun inputMetrics(
    size: YappInputSize,
    variant: YappTextFieldVariant = YappTextFieldVariant.Default,
) = if (variant == YappTextFieldVariant.Auth) {
    InputMetrics(
        height = 54.dp,
        horizontalPadding = 16.dp,
        cornerRadius = 12.dp,
        iconSpacing = 12.dp,
    )
} else {
    when (size) {
        YappInputSize.Large -> InputMetrics(48.dp, 16.dp, 10.dp, 12.dp)
        YappInputSize.Medium -> InputMetrics(40.dp, 10.dp, 8.dp, 12.dp)
        YappInputSize.Small -> InputMetrics(32.dp, 8.dp, 6.dp, 8.dp)
    }
}

private fun inputBorderColor(
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

private fun supportingTextColor(state: YappInputState) =
    when (state) {
        YappInputState.Success -> YappColor.success
        YappInputState.Error -> YappColor.error
        YappInputState.Default,
        YappInputState.Active,
        -> YappColor.textPrimary
    }

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappInputPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = YappColor.white) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                YappTextField(
                    value = "",
                    onValueChange = {},
                    title = "이름",
                    state = YappInputState.Active,
                    supportingText = "텍스트를 입력해주세요",
                    required = true,
                )
                YappTextField(
                    value = "잘못된 입력",
                    onValueChange = {},
                    title = "이메일",
                    size = YappInputSize.Medium,
                    state = YappInputState.Error,
                    supportingText = "이메일 형식으로 입력해주세요",
                )
                YappTextArea(
                    value = "",
                    onValueChange = {},
                    title = "내용",
                    size = YappTextAreaSize.Medium,
                    required = true,
                )
            }
        }
    }
}
