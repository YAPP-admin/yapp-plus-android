package com.yapp.plus.core.designsystem.component.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yapp.plus.core.designsystem.component.text.YappText
import com.yapp.plus.core.designsystem.theme.YappColor
import com.yapp.plus.core.designsystem.theme.YappTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YappBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showHandle: Boolean = false,
    title: String?,
    body: String?,
    confirmText: String?,
    dismissText: String?,
    showActions: Boolean = true,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val actualSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val handle: @Composable (() -> Unit)? =
        if (showHandle) {
            { YappBottomSheetHandle(contentDescription = contentDescription) }
        } else {
            null
        }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        sheetState = actualSheetState,
        shape = RoundedCornerShape(20.dp),
        containerColor = YappColor.white,
        contentColor = YappColor.textPrimary,
        tonalElevation = 0.dp,
        scrimColor = YappColor.scrim,
        dragHandle = handle,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
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
                            textAlign = TextAlign.Center,
                        )
                    }
                    body?.let {
                        YappText(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            style =
                                YappTypography.body2NormalRegular.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    letterSpacing = 0.203.sp,
                                ),
                            color = YappColor.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            content()

            if (showActions && (confirmText != null || dismissText != null)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    confirmText?.let {
                        YappPopupActionButton(
                            text = it,
                            primary = true,
                            height = 48.dp,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onConfirm,
                        )
                    }
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

@Composable
private fun YappBottomSheetHandle(contentDescription: String?) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .semantics {
                    if (contentDescription != null) this.contentDescription = contentDescription
                },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(YappColor.scrollIndicator),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun YappBottomSheetPreview() {
    YappBottomSheet(
        onDismissRequest = {},
        onConfirm = {},
        onDismiss = {},
        title = "제목",
        body = "내용",
        confirmText = "권장행동",
        dismissText = "행동",
        showHandle = true,
        content = {
            Text(
                text = "선택 항목을 여기에 표시합니다.",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(YappColor.orangeWeak)
                        .padding(16.dp),
                color = YappColor.textSecondary,
            )
        },
    )
}
