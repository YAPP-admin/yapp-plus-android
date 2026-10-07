package com.yapp.plus.core.designsystem.component.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

private const val COMPACT_DIALOG_DIM_AMOUNT = 0.45f
private const val DEFAULT_DIALOG_DIM_AMOUNT = 0.52f
private val compactDialogMaxWidth = 285.dp

@Composable
fun YappAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String?,
    body: String?,
    confirmText: String,
    dismissText: String?,
    buttonLayout: YappAlertButtonLayout = YappAlertButtonLayout.Horizontal,
    showActions: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    variant: YappAlertDialogVariant = YappAlertDialogVariant.Default,
) {
    val isCompact = variant == YappAlertDialogVariant.Compact
    val shape = RoundedCornerShape(if (isCompact) 12.dp else 20.dp)
    val elevation = if (isCompact) 0.dp else 36.dp
    val horizontalPadding = if (isCompact) 20.dp else 16.dp
    val widthModifier = if (isCompact) Modifier.widthIn(max = compactDialogMaxWidth) else Modifier
    val textAlign = if (isCompact) TextAlign.Start else TextAlign.Center
    val titleColor = if (isCompact) YappColor.foregroundNeutral else YappColor.textPrimary
    val bodyColor = if (isCompact) YappColor.foregroundMuted else YappColor.textSecondary
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.setDimAmount(
                if (isCompact) COMPACT_DIALOG_DIM_AMOUNT else DEFAULT_DIALOG_DIM_AMOUNT,
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =
                    modifier
                        .then(widthModifier)
                        .fillMaxWidth()
                        .shadow(elevation = elevation, shape = shape)
                        .clip(shape)
                        .background(YappColor.white)
                        .padding(horizontal = horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (title != null || body != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 8.dp),
                    ) {
                        title?.let {
                            YappText(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style =
                                    if (isCompact) {
                                        YappTypography.headline1Bold.copy(letterSpacing = 0.sp)
                                    } else {
                                        YappTypography.headline1Bold
                                    },
                                color = titleColor,
                                textAlign = textAlign,
                            )
                        }
                        body?.let {
                            YappText(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style =
                                    if (isCompact) {
                                        YappTypography.label1NormalMedium.copy(letterSpacing = 0.sp)
                                    } else {
                                        YappTypography.body2NormalRegular.copy(
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            letterSpacing = 0.203.sp,
                                        )
                                    },
                                color = bodyColor,
                                textAlign = textAlign,
                            )
                        }
                    }
                }

                if (showActions) {
                    when (buttonLayout) {
                        YappAlertButtonLayout.Horizontal -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                if (dismissText != null) {
                                    YappPopupActionButton(
                                        text = dismissText,
                                        primary = false,
                                        height = if (isCompact) 44.dp else 56.dp,
                                        variant = variant,
                                        modifier = Modifier.weight(1f),
                                        onClick = onDismiss,
                                    )
                                }
                                YappPopupActionButton(
                                    text = confirmText,
                                    primary = true,
                                    height = if (isCompact) 44.dp else 56.dp,
                                    variant = variant,
                                    modifier = Modifier.weight(1f),
                                    onClick = onConfirm,
                                )
                            }
                        }

                        YappAlertButtonLayout.Vertical -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                YappPopupActionButton(
                                    text = confirmText,
                                    primary = true,
                                    height = if (isCompact) 44.dp else 48.dp,
                                    variant = variant,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onConfirm,
                                )
                                dismissText?.let {
                                    YappPopupActionButton(
                                        text = it,
                                        primary = false,
                                        height = if (isCompact) 44.dp else 48.dp,
                                        variant = variant,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = onDismiss,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappAlertDialogPreview() {
    YappAlertDialog(
        onDismissRequest = {},
        title = "제목",
        body = "내용",
        confirmText = "권장행동",
        dismissText = "행동",
        onConfirm = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappAlertDialogVerticalPreview() {
    YappAlertDialog(
        onDismissRequest = {},
        title = "제목",
        body = "내용",
        confirmText = "권장행동",
        dismissText = "행동",
        buttonLayout = YappAlertButtonLayout.Vertical,
        onConfirm = {},
        onDismiss = {},
    )
}
