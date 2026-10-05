package com.yapp.plus.core.designsystem.component.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.R
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography

@Composable
fun YappTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    placeholder: String = "내용을 입력해주세요",
    supportingText: String? = null,
    size: YappInputSize = YappInputSize.Large,
    state: YappInputState = YappInputState.Default,
    enabled: Boolean = true,
    required: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val metrics = inputMetrics(size)
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            YappText(title, style = YappTypography.label1NormalMedium, color = LabelColor)
            Spacer(Modifier.height(4.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(metrics.height)
                    .border(1.dp, inputBorderColor(state), RoundedCornerShape(metrics.cornerRadius))
                    .padding(horizontal = metrics.horizontalPadding),
                enabled = enabled,
                singleLine = true,
                textStyle = inputTextStyle(size).copy(color = LabelColor),
                cursorBrush = SolidColor(PrimaryColor),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(metrics.iconSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leadingIcon?.invoke()
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                YappText(placeholder, style = inputTextStyle(size), color = PlaceholderColor)
                            }
                            innerTextField()
                        }
                        trailingIcon?.invoke()
                    }
                }
            )
            if (supportingText != null) {
                Spacer(Modifier.height(8.dp))
                YappText(
                    text = supportingText,
                    style = YappTypography.label2Regular,
                    color = supportingTextColor(state)
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
    required: Boolean = false
) {
    val inputSize = if (size == YappTextAreaSize.Large) YappInputSize.Large else YappInputSize.Medium
    val metrics = inputMetrics(inputSize)
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            YappText(title, style = YappTypography.label1NormalMedium, color = LabelColor)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(1.dp, inputBorderColor(state), RoundedCornerShape(metrics.cornerRadius))
                    .padding(horizontal = metrics.horizontalPadding, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { if (it.length <= maxLength) onValueChange(it) },
                    modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
                    enabled = enabled,
                    textStyle = inputTextStyle(inputSize).copy(color = LabelColor),
                    cursorBrush = SolidColor(PrimaryColor),
                    decorationBox = { innerTextField ->
                        Box(Modifier.fillMaxSize()) {
                            if (value.isEmpty()) {
                                YappText(
                                    placeholder,
                                    style = inputTextStyle(inputSize),
                                    color = PlaceholderColor
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                YappText(
                    text = "${value.length} / $maxLength",
                    modifier = Modifier.align(Alignment.BottomEnd),
                    style = YappTypography.label2Regular,
                    color = HelperColor
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
        modifier = Modifier.offset(x = (-8).dp, y = (-8).dp).size(16.dp)
    )
}

private fun inputTextStyle(size: YappInputSize): TextStyle = when (size) {
    YappInputSize.Large -> YappTypography.body1NormalRegular
    YappInputSize.Medium -> YappTypography.body2NormalRegular
    YappInputSize.Small -> YappTypography.label2Regular
}

private fun inputMetrics(size: YappInputSize) = when (size) {
    YappInputSize.Large -> InputMetrics(48.dp, 16.dp, 10.dp, 12.dp)
    YappInputSize.Medium -> InputMetrics(40.dp, 10.dp, 8.dp, 12.dp)
    YappInputSize.Small -> InputMetrics(32.dp, 8.dp, 6.dp, 8.dp)
}

private fun inputBorderColor(state: YappInputState) = when (state) {
    YappInputState.Default -> NeutralBorderColor
    YappInputState.Active -> PrimaryColor
    YappInputState.Success -> StrongBorderColor
    YappInputState.Error -> ErrorColor
}

private fun supportingTextColor(state: YappInputState) = when (state) {
    YappInputState.Success -> SuccessColor
    YappInputState.Error -> ErrorColor
    YappInputState.Default,
    YappInputState.Active -> LabelColor
}

private val LabelColor = Color(0xFF171719)
private val PlaceholderColor = Color(0x4737383C)
private val HelperColor = Color(0xFF5C5C5C)
private val PrimaryColor = Color(0xFFFA6027)
private val NeutralBorderColor = Color(0x3870737C)
private val StrongBorderColor = Color(0x8570737C)
private val SuccessColor = Color(0xFF00BF40)
private val ErrorColor = Color(0xFFFF4242)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun YappInputPreview() {
    YappTheme(dynamicColor = false) {
        Surface(color = Color.White) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                YappTextField(
                    value = "",
                    onValueChange = {},
                    title = "이름",
                    state = YappInputState.Active,
                    supportingText = "텍스트를 입력해주세요",
                    required = true
                )
                YappTextField(
                    value = "잘못된 입력",
                    onValueChange = {},
                    title = "이메일",
                    size = YappInputSize.Medium,
                    state = YappInputState.Error,
                    supportingText = "이메일 형식으로 입력해주세요"
                )
                YappTextArea(
                    value = "",
                    onValueChange = {},
                    title = "내용",
                    size = YappTextAreaSize.Medium,
                    required = true
                )
            }
        }
    }
}
