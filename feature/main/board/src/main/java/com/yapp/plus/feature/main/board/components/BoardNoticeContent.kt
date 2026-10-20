package com.yapp.plus.feature.main.board.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yapp.plus.core.designsystem.components.button.YappButton
import com.yapp.plus.core.designsystem.components.button.YappButtonSize
import com.yapp.plus.core.designsystem.components.text.YappText
import com.yapp.plus.core.designsystem.theme.YappMainColor
import com.yapp.plus.core.designsystem.theme.YappTypography
import com.yapp.plus.feature.main.board.R
import com.yapp.plus.feature.main.board.model.BoardNoticeUiModel
import com.yapp.plus.feature.main.board.model.NoticeUiState

@Composable
internal fun <T> BoardNoticeState(
    state: NoticeUiState<T>,
    onRetry: () -> Unit,
    @StringRes emptyMessage: Int,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        when (state) {
            NoticeUiState.Loading -> CircularProgressIndicator(color = YappMainColor.brand)
            NoticeUiState.Empty -> BoardEmptyState(message = emptyMessage)
            NoticeUiState.Error ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    YappText(
                        stringResource(R.string.board_error),
                        YappTypography.body1NormalRegular,
                        YappMainColor.textPrimary,
                    )
                    val retryLabel = stringResource(R.string.board_retry)
                    YappButton(retryLabel, onRetry, size = YappButtonSize.Medium)
                }
            is NoticeUiState.Content -> content(state.value)
        }
    }
}

@Composable
internal fun BoardNoticeCard(
    notice: BoardNoticeUiModel,
    onClick: () -> Unit,
) {
    val categoryLabel = stringResource(notice.category.labelResource)
    Surface(onClick = onClick, color = YappMainColor.surface) {
        Column(Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
            YappText(
                notice.title,
                YappTypography.body1NormalBold,
                YappMainColor.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            YappText(
                stringResource(R.string.board_metadata, categoryLabel, notice.date),
                YappTypography.caption1Bold,
                YappMainColor.textSecondary,
                Modifier.padding(top = 8.dp),
            )
        }
    }
    HorizontalDivider(color = YappMainColor.divider)
}

@Composable
internal fun BoardNoticeDetail(notice: BoardNoticeUiModel) {
    val categoryLabel = stringResource(notice.category.labelResource)
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        YappText(notice.title, YappTypography.heading1Bold, YappMainColor.textPrimary)
        YappText(
            stringResource(R.string.board_metadata, categoryLabel, notice.date),
            YappTypography.caption1Bold,
            YappMainColor.textSecondary,
        )
        HorizontalDivider(color = YappMainColor.divider)
        YappText(notice.content, YappTypography.body1NormalRegular, YappMainColor.textPrimary)
    }
}
