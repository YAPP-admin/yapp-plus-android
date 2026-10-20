package com.yapp.plus.feature.main.home.model

import com.yapp.plus.domain.notice.Notice
import com.yapp.plus.domain.notice.NoticeType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

sealed interface HomeNoticeUiState {
    data object Loading : HomeNoticeUiState

    data object Error : HomeNoticeUiState

    data object Empty : HomeNoticeUiState

    data class Content(
        val notices: List<HomeNoticeUiModel>,
    ) : HomeNoticeUiState
}

internal fun List<Notice>.toHomeNoticeState(
    today: LocalDate = LocalDate.now(ZoneId.of("Asia/Seoul")),
): HomeNoticeUiState {
    val zone = ZoneId.of("Asia/Seoul")
    val sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val start = sunday.atStartOfDay(zone).toInstant()
    val end = sunday.plusWeeks(1).atStartOfDay(zone).toInstant()
    val notices =
        filter { it.publishedAt >= start && it.publishedAt < end }
            .map { HomeNoticeUiModel(it.id, it.title, it.type == NoticeType.SESSION) }
    return if (notices.isEmpty()) HomeNoticeUiState.Empty else HomeNoticeUiState.Content(notices)
}
