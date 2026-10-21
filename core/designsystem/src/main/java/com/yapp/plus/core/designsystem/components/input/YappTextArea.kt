package com.yapp.plus.core.designsystem.components.input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    placeholder: String,
    modifier: Modifier = Modifier,
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
    val metrics = InputMetrics.from(inputSize)
    val borderColor = inputBorderColor(state)
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
                CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
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
                }
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

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappTextAreaPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = YappColor.white) {
            Box(modifier = Modifier.padding(20.dp)) {
                YappTextArea(
                    value = "",
                    onValueChange = {},
                    title = "내용",
                    placeholder = "내용을 입력해주세요",
                    size = YappTextAreaSize.Medium,
                    required = true,
                )
            }
        }
    }
}
