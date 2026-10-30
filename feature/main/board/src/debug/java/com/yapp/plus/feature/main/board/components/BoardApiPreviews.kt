package com.yapp.plus.feature.main.board.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.domain.notice.NoticeAssignment
import com.yapp.plus.domain.notice.NoticeImage
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import java.time.Instant
import java.time.LocalDate

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun BoardAssignmentPreview() {
    YappTheme {
        BoardAssignmentInfo(
            NoticeAssignment(
                LocalDate.of(2026, 10, 30),
                listOf("DESIGN", "ANDROID"),
                "https://example.com",
                "과제 제출",
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardAssignmentRowPreview() {
    YappTheme {
        BoardAssignmentRow("제출 기한") {
            YappText("2026년 10월 30일 (금)", YappTypography.scaleT4Medium, YappMainColor.textSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardDeadlinePreview() {
    YappTheme { BoardDeadlineBadge(2) }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun BoardMarkdownPreview() {
    YappTheme { BoardMarkdown("**중요한 안내**\n\n- 일정과 장소를 확인해 주세요.\n- [링크](https://example.com)") }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun BoardImagePreview() {
    YappTheme { BoardNoticeImage(NoticeImage("image", "첨부 이미지", "", Instant.EPOCH), {}) }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun BoardReadPreview() {
    YappTheme {
        BoardReadConfirmation(
            BoardNoticeUiModel(
                1,
                "공지",
                "본문",
                BoardCategory.Session,
                "2026.10.30",
                readCount = 95,
            ),
            {
            },
            false,
            false,
            {},
        )
    }
}
