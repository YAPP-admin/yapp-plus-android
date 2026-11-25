package com.yapp.plus.core.designsystem.components.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

private const val AUTH_ERROR_DIALOG_DIM_AMOUNT = 0.455f
private const val COMPACT_DIALOG_DIM_AMOUNT = 0.45f
private const val DEFAULT_DIALOG_DIM_AMOUNT = 0.52f
private val compactDialogMaxWidth = 285.dp

// Android rounds glyph widths, which can wrap the auth title at the design width.
private val authErrorTitleStyle =
    YappTypography.scaleT6Bold.copy(
        textGeometricTransform = TextGeometricTransform(scaleX = 0.99f),
    )

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
    compactWidth: Dp = compactDialogMaxWidth,
    content: (@Composable () -> Unit)? = null,
) {
    val isCompact = variant.isCompact
    val shape = RoundedCornerShape(if (isCompact) 16.dp else 20.dp)
    val elevation = if (isCompact) 0.dp else 36.dp
    val horizontalPadding = if (isCompact) 20.dp else 16.dp
    val widthModifier = if (isCompact) Modifier.widthIn(max = compactWidth) else Modifier
    val textAlign = if (isCompact) TextAlign.Start else TextAlign.Center
    val titleStyle =
        when {
            variant == YappAlertDialogVariant.AuthError -> authErrorTitleStyle
            isCompact -> YappTypography.scaleT6Bold
            else -> YappTypography.headline1Bold
        }
    val titleColor = if (isCompact) YappColor.foregroundNeutral else YappColor.textPrimary
    val bodyColor = if (isCompact) YappColor.foregroundMuted else YappColor.textSecondary
    val actionHeight =
        when {
            variant == YappAlertDialogVariant.AuthError -> 40.dp
            isCompact -> 44.dp
            buttonLayout == YappAlertButtonLayout.Horizontal -> 56.dp
            else -> 48.dp
        }
    val dimAmount =
        when (variant) {
            YappAlertDialogVariant.AuthError -> AUTH_ERROR_DIALOG_DIM_AMOUNT
            YappAlertDialogVariant.Compact -> COMPACT_DIALOG_DIM_AMOUNT
            YappAlertDialogVariant.Default -> DEFAULT_DIALOG_DIM_AMOUNT
        }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.setDimAmount(dimAmount)
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
                                style = titleStyle,
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
                                        YappTypography.scaleT4Medium
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
                content?.invoke()

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
                                        height = actionHeight,
                                        variant = variant,
                                        modifier = Modifier.weight(1f),
                                        onClick = onDismiss,
                                    )
                                }
                                YappPopupActionButton(
                                    text = confirmText,
                                    primary = true,
                                    height = actionHeight,
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
                                    height = actionHeight,
                                    variant = variant,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onConfirm,
                                )
                                dismissText?.let {
                                    YappPopupActionButton(
                                        text = it,
                                        primary = false,
                                        height = actionHeight,
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

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappAlertDialogProgressPreview() {
    YappAlertDialog(
        onDismissRequest = {},
        title = null,
        body = null,
        confirmText = "",
        dismissText = null,
        showActions = false,
        onConfirm = {},
        onDismiss = {},
        variant = YappAlertDialogVariant.Compact,
        content = {
            Box(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        },
    )
}
