package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.AfterpartySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
internal class AfterpartyBannerViewModel
    @Inject
    constructor(
        private val repository: AfterpartyRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow<AfterpartySummary?>(null)
        val state = mutableState.asStateFlow()
        private var load: Job? = null

        fun refresh() {
            if (load?.isActive == true) return
            load =
                viewModelScope.launch {
                    val page = repository.fetchAfterparties(AfterpartyFilter.OPEN).valueOrNull()
                    mutableState.value = page?.items?.firstOrNull { Instant.now().isBefore(it.schedule.deadline) }
                }
        }
    }
