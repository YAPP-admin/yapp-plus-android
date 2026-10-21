package com.yapp.plus.feature.main.my.model

sealed interface MyProfileSummaryState {
    data object Loading : MyProfileSummaryState

    data object Empty : MyProfileSummaryState

    data object Error : MyProfileSummaryState

    @JvmInline
    value class Content(
        val displayName: String,
    ) : MyProfileSummaryState
}
