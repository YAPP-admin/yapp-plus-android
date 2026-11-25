package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartySummary

internal data class CommunityListState(
    val filter: AfterpartyFilter = AfterpartyFilter.ALL,
    val items: List<AfterpartySummary> = emptyList(),
    val nextCursor: String? = null,
    val loading: LoadingIndicator? = LoadingIndicator.List,
    val hasError: Boolean = false,
) {
    enum class LoadingIndicator {
        List,
        PullToRefresh,
    }
}
