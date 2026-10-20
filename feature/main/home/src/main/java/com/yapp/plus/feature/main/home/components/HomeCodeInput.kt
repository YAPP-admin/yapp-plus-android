package com.yapp.plus.feature.main.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappHomeColor
import com.yapp.plus.core.designsystem.theme.YappHomeTypography
import com.yapp.plus.feature.main.home.R

@Composable
internal fun HomeCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    enabled: Boolean,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val description = stringResource(R.string.home_code_accessibility)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().semantics { contentDescription = description },
        enabled = enabled,
        singleLine = true,
        interactionSource = interactionSource,
        textStyle = YappHomeTypography.heading.copy(color = Color.Transparent),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done,
            ),
        keyboardActions = KeyboardActions(onDone = { onConfirm() }),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                innerTextField()
                Row(
                    Modifier.clearAndSetSemantics {},
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    repeat(CODE_LENGTH) { index ->
                        HomeCodeDigit(
                            digit = value.getOrNull(index),
                            isActive = isFocused && index == value.length,
                            isError = isError,
                        )
                    }
                }
            }
        },
    )
}

@Composable
internal fun HomeCodeDigit(
    digit: Char?,
    isActive: Boolean,
    isError: Boolean,
) {
    val border =
        when {
            isError -> YappHomeColor.error
            isActive -> YappHomeColor.muted
            else -> YappHomeColor.border
        }
    val color =
        when {
            isError -> YappHomeColor.error
            digit != null -> YappHomeColor.text
            else -> YappHomeColor.placeholder
        }
    Box(
        Modifier.size(54.dp).border(1.dp, border, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (isActive && !isError) {
            Box(
                Modifier.width(2.dp).height(22.dp).background(YappHomeColor.muted),
            )
        } else {
            YappText(
                digit?.toString() ?: "0",
                YappHomeTypography.heading,
                color,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private const val CODE_LENGTH = 4
