package com.yapp.plus.feature.main.board.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.notice.NoticeAssignment
import com.yapp.plus.feature.main.board.R
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.yapp.plus.core.designsystem.R as DesignSystemR

@Composable
internal fun BoardAssignmentInfo(assignment: NoticeAssignment) {
    val handler = LocalUriHandler.current
    Surface(color = YappMainColor.basement, shape = RoundedCornerShape(8.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BoardAssignmentRow(stringResource(R.string.board_assignment_deadline)) {
                YappText(
                    assignment.deadlineDate.format(
                        DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN),
                    ),
                    YappTypography.scaleT4Medium,
                    YappMainColor.textSecondary,
                )
            }
            BoardAssignmentRow(stringResource(R.string.board_assignment_positions)) {
                YappText(
                    assignment.positions.joinToString(", ") { positionLabel(it) },
                    YappTypography.scaleT4Medium,
                    YappMainColor.textSecondary,
                )
            }
            BoardAssignmentRow(stringResource(R.string.board_assignment_method)) {
                Row(
                    Modifier.clickable {
                        runCatching {
                            handler.openUri(
                                assignment.submissionUrl,
                            )
                        }
                    },
                ) {
                    Icon(
                        painterResource(DesignSystemR.drawable.ic_link),
                        null,
                        Modifier.size(16.dp),
                        tint = YappMainColor.textSecondary,
                    )
                    YappText(
                        assignment.submissionLinkText,
                        YappTypography.scaleT4Medium.copy(
                            textDecoration = TextDecoration.Underline,
                        ),
                        YappMainColor.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
internal fun BoardAssignmentRow(
    label: String,
    value: @Composable () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        YappText(
            label,
            YappTypography.scaleT4Bold,
            YappMainColor.textPrimary,
            Modifier.width(48.dp),
        )
        value()
    }
}

private fun positionLabel(value: String): String =
    when (value) {
        "DESIGN" -> "Designer"
        "ANDROID" -> "Android"
        "IOS" -> "iOS"
        "MOBILE" -> "Mobile"
        "WEB" -> "Web"
        "SERVER" -> "Server"
        "AI_NATIVE" -> "AI Native"
        else -> value
    }
