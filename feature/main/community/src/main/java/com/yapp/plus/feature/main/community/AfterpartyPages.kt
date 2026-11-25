package com.yapp.plus.feature.main.community

import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyPage
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.result.YappResult

internal fun <T> AfterpartyResult<T>?.valueOrNull(): T? = (this as? YappResult.Success)?.value

internal suspend fun <T> fetchAllPages(
    idOf: (T) -> Long,
    fetch: suspend (String?) -> AfterpartyResult<AfterpartyPage<T>>,
): AfterpartyResult<List<T>> {
    val cursors = mutableSetOf<String>()
    val items = linkedMapOf<Long, T>()
    var cursor: String? = null
    do {
        val page =
            when (val result = fetch(cursor)) {
                is YappResult.Failure -> return result
                is YappResult.Success -> result.value
            }
        page.items.forEach { items[idOf(it)] = it }
        cursor = page.nextCursor
        if (cursor != null && !cursors.add(cursor)) {
            return YappResult.Failure(AfterpartyFailure.Unavailable)
        }
    } while (cursor != null)
    return YappResult.Success(items.values.toList())
}
