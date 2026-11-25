package com.yapp.plus.domain.afterparty

data class AfterpartyPage<T>(
    val items: List<T>,
    val nextCursor: String?,
)
