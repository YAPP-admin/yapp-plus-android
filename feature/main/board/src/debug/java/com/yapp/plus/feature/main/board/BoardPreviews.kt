package com.yapp.plus.feature.main.board

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.yapp.plus.core.designsystem.theme.YappTheme
import com.yapp.plus.core.preview.EnumPreviewParameterProvider
import com.yapp.plus.feature.main.board.components.BoardCategoryChip
import com.yapp.plus.feature.main.board.components.BoardCategorySelector
import com.yapp.plus.feature.main.board.components.BoardEmptyState
import com.yapp.plus.feature.main.board.components.BoardNoticeBadge
import com.yapp.plus.feature.main.board.components.BoardNoticeCard
import com.yapp.plus.feature.main.board.components.BoardNoticeDetail
import com.yapp.plus.feature.main.board.components.BoardNoticeState
import com.yapp.plus.feature.main.board.model.BoardCategory
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState

@Preview(name = "Board", widthDp = 393, heightDp = 692)
@Preview(name = "Tall board", widthDp = 393, heightDp = 852)
@Composable
private fun BoardScreenPreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { BoardScreen() }
}

@Preview(name = "Board category selector", widthDp = 353, showBackground = true)
@Composable
private fun BoardCategorySelectorPreview(
    @PreviewParameter(BoardCategoryPreviewParameterProvider::class) category: BoardCategory,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        BoardCategorySelector(selectedCategory = category, onCategorySelected = {})
    }
}

@Preview(name = "Board category tab", showBackground = true)
@Composable
private fun BoardCategoryChipPreview(
    @PreviewParameter(BoardCategoryPreviewParameterProvider::class) category: BoardCategory,
) {
    YappTheme(darkTheme = false, dynamicColor = false) {
        Row {
            BoardCategoryChip(category = category, isSelected = true, onClick = {})
            BoardCategoryChip(category = category, isSelected = false, onClick = {})
        }
    }
}

@Preview(name = "Board empty state", widthDp = 353, heightDp = 240, showBackground = true)
@Composable
private fun BoardEmptyStatePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) { BoardEmptyState() }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun BoardNoticeCardPreview() {
    YappTheme { BoardNoticeCard(previewNotice(), {}) }
}

@Preview(widthDp = 353, showBackground = true)
@Composable
private fun BoardNoticeDetailPreview() {
    YappTheme { BoardNoticeDetail(previewNotice()) }
}

@Preview(widthDp = 353, heightDp = 240, showBackground = true)
@Composable
private fun BoardNoticeStatePreview() {
    YappTheme { BoardNoticeState<Nothing>(NoticeUiState.Error, {}, R.string.board_empty) {} }
}

private fun previewNotice() =
    BoardNoticeUiModel(
        1,
        "공지 제목",
        "공지 본문을 확인해 주세요.",
        BoardCategory.Operation,
        "2026.10.20",
    )

private class BoardCategoryPreviewParameterProvider :
    EnumPreviewParameterProvider<BoardCategory>(BoardCategory.entries)

@Preview(showBackground = true)
@Composable
private fun BoardNoticeBadgePreview() {
    YappTheme(darkTheme = false, dynamicColor = false) {
        BoardNoticeBadge(BoardCategory.Session)
    }
}
