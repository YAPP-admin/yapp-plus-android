package com.yapp.plus.core.designsystem.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier =
    clickable(
        interactionSource = null,
        indication = null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )

fun Modifier.noRippleSelectable(
    selected: Boolean,
    role: Role,
    onClick: () -> Unit,
): Modifier =
    selectable(
        selected = selected,
        interactionSource = null,
        indication = null,
        role = role,
        onClick = onClick,
    )

fun Modifier.noRippleToggleable(
    value: Boolean,
    enabled: Boolean,
    role: Role,
    onValueChange: (Boolean) -> Unit,
): Modifier =
    toggleable(
        value = value,
        interactionSource = null,
        indication = null,
        enabled = enabled,
        role = role,
        onValueChange = onValueChange,
    )

@Composable
fun WithoutRipple(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalRippleConfiguration provides null, content = content)
}
