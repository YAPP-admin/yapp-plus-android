package com.yapp.plus.core.designsystem.component.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

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
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0.52f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .shadow(elevation = 36.dp, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(YappColor.white)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (title != null || body != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        title?.let {
                            YappText(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style = YappTypography.headline1Bold,
                                color = YappColor.textPrimary,
                            )
                        }
                        body?.let {
                            YappText(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style = YappTypography.body2NormalRegular.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    letterSpacing = 0.203.sp,
                                ),
                                color = YappColor.textSecondary,
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
                                        height = 56.dp,
                                        modifier = Modifier.weight(1f),
                                        onClick = onDismiss,
                                    )
                                }
                                YappPopupActionButton(
                                    text = confirmText,
                                    primary = true,
                                    height = 56.dp,
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
                                    height = 48.dp,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onConfirm,
                                )
                                dismissText?.let {
                                    YappPopupActionButton(
                                        text = it,
                                        primary = false,
                                        height = 48.dp,
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
