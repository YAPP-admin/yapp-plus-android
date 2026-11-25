package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.result.YappResult
import com.yapp.plus.feature.main.community.CommunityListState.LoadingIndicator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CommunityViewModel
    @Inject
    constructor(
        private val repository: AfterpartyRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(CommunityListState())
        val state = mutableState.asStateFlow()
        private var load: Job? = null
        private val cursors = mutableSetOf<String>()

        fun refresh(filter: AfterpartyFilter = state.value.filter) {
            reload(filter, LoadingIndicator.List)
        }

        fun pullToRefresh() {
            reload(state.value.filter, LoadingIndicator.PullToRefresh)
        }

        private fun reload(
            filter: AfterpartyFilter,
            indicator: LoadingIndicator,
        ) {
            load?.cancel()
            cursors.clear()
            val previous = state.value
            mutableState.value =
                if (previous.filter == filter) {
                    previous.copy(loading = indicator, hasError = false)
                } else {
                    CommunityListState(filter = filter, loading = indicator)
                }
            request(filter, cursor = null)
        }

        fun loadMore() {
            val current = state.value
            val cursor = current.nextCursor ?: return
            if (load?.isActive == true || current.hasError) return
            mutableState.value = current.copy(loading = LoadingIndicator.List)
            request(current.filter, cursor)
        }

        private fun request(
            filter: AfterpartyFilter,
            cursor: String?,
        ) {
            load =
                viewModelScope.launch {
                    val result = repository.fetchAfterparties(filter, cursor)
                    ensureActive()
                    val current = state.value
                    when (result) {
                        is YappResult.Failure -> mutableState.value = current.copy(loading = null, hasError = true)
                        is YappResult.Success -> {
                            val page = result.value
                            val next = page.nextCursor
                            val repeating = next != null && !cursors.add(next)
                            val previous = if (cursor != null) current.items else emptyList()
                            mutableState.value =
                                current.copy(
                                    items = (previous + page.items).distinctBy { it.id },
                                    nextCursor = next.takeUnless { repeating },
                                    loading = null,
                                    hasError = repeating,
                                )
                        }
                    }
                }
        }
    }
