package com.yapp.plus.core.designsystem.component.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappTypography

private val LabelColor = Color(0xFF171719)
private val NeutralLabelColor = Color(0xFF2E2F33)
private val PrimaryColor = Color(0xFFFA6027)
private val LineColor = Color(0xFF70737C).copy(alpha = 0.22f)
private val ScrimColor = Color(0xFF171719)

enum class YappAlertButtonLayout {
    Horizontal,
    Vertical
}

@Composable
fun YappAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = "제목",
    body: String? = "내용",
    confirmText: String = "권장행동",
    dismissText: String? = "행동",
    buttonLayout: YappAlertButtonLayout = YappAlertButtonLayout.Horizontal,
    showActions: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0.52f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .shadow(elevation = 36.dp, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (title != null || body != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        title?.let {
                            YappText(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style = YappTypography.headline1Bold,
                                color = LabelColor
                            )
                        }
                        body?.let {
                            YappText(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style = YappTypography.body2NormalRegular.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    letterSpacing = 0.203.sp
                                ),
                                color = NeutralLabelColor
                            )
                        }
                    }
                }

                if (showActions) {
                    when (buttonLayout) {
                        YappAlertButtonLayout.Horizontal -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (dismissText != null) {
                                    YappPopupActionButton(
                                        text = dismissText,
                                        primary = false,
                                        height = 56.dp,
                                        modifier = Modifier.weight(1f),
                                        onClick = onDismiss
                                    )
                                }
                                YappPopupActionButton(
                                    text = confirmText,
                                    primary = true,
                                    height = 56.dp,
                                    modifier = Modifier.weight(1f),
                                    onClick = onConfirm
                                )
                            }
                        }

                        YappAlertButtonLayout.Vertical -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                YappPopupActionButton(
                                    text = confirmText,
                                    primary = true,
                                    height = 48.dp,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onConfirm
                                )
                                dismissText?.let {
                                    YappPopupActionButton(
                                        text = it,
                                        primary = false,
                                        height = 48.dp,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = onDismiss
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YappBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showHandle: Boolean = false,
    title: String? = "제목",
    body: String? = "내용",
    confirmText: String? = "권장행동",
    dismissText: String? = "행동",
    showActions: Boolean = true,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val actualSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val handle: @Composable (() -> Unit)? = if (showHandle) {
        { YappBottomSheetHandle(contentDescription = contentDescription) }
    } else {
        null
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        sheetState = actualSheetState,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        contentColor = LabelColor,
        tonalElevation = 0.dp,
        scrimColor = ScrimColor.copy(alpha = 0.28f),
        dragHandle = handle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (title != null || body != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    title?.let {
                        YappText(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            style = YappTypography.headline1Bold,
                            color = LabelColor
                        )
                    }
                    body?.let {
                        YappText(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            style = YappTypography.body2NormalRegular.copy(
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                letterSpacing = 0.203.sp
                            ),
                            color = NeutralLabelColor
                        )
                    }
                }
            }

            content()

            if (showActions && (confirmText != null || dismissText != null)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    confirmText?.let {
                        YappPopupActionButton(
                            text = it,
                            primary = true,
                            height = 48.dp,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onConfirm
                        )
                    }
                    dismissText?.let {
                        YappPopupActionButton(
                            text = it,
                            primary = false,
                            height = 48.dp,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YappBottomSheetHandle(contentDescription: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF70737C).copy(alpha = 0.16f))
        )
    }
}

@Composable
private fun YappPopupActionButton(
    text: String,
    primary: Boolean,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(if (primary) 12.dp else 10.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = shape,
        color = if (primary) PrimaryColor else Color.White,
        border = if (primary) null else BorderStroke(1.dp, LineColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = YappTypography.body1NormalBold,
                color = if (primary) Color.White else PrimaryColor
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappAlertDialogPreview() {
    YappAlertDialog(
        onDismissRequest = {},
        onConfirm = {},
        onDismiss = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappAlertDialogVerticalPreview() {
    YappAlertDialog(
        onDismissRequest = {},
        buttonLayout = YappAlertButtonLayout.Vertical,
        onConfirm = {},
        onDismiss = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappBottomSheetPreview() {
    YappBottomSheet(
        onDismissRequest = {},
        onConfirm = {},
        onDismiss = {},
        showHandle = true,
        content = {
            Text(
                text = "선택 항목을 여기에 표시합니다.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFFFEFE9))
                    .padding(16.dp),
                color = NeutralLabelColor
            )
        }
    )
}
