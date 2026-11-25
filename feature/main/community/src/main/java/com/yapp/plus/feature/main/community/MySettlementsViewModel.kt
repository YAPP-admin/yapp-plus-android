package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyFilter
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.afterparty.AfterpartySummary
import com.yapp.plus.domain.afterparty.SettlementItem
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MySettlementsViewModel
    @Inject
    constructor(
        private val parties: AfterpartyRepository,
        private val settlements: SettlementRepository,
    ) : ViewModel() {
        private val mutableState =
            MutableStateFlow<AfterpartyResult<List<Pair<AfterpartySummary, SettlementItem>>>?>(null)
        val state = mutableState.asStateFlow()
        private var load: Job? = null

        fun refresh() {
            if (load?.isActive == true) return
            load = viewModelScope.launch { mutableState.value = fetchRows() }
        }

        private suspend fun fetchRows(): AfterpartyResult<List<Pair<AfterpartySummary, SettlementItem>>> {
            val afterparties =
                when (
                    val result =
                        fetchAllPages({ item -> item.id }) {
                            parties.fetchAfterparties(AfterpartyFilter.ALL, it)
                        }
                ) {
                    is YappResult.Failure -> return result
                    is YappResult.Success -> result.value
                }
            val rows = mutableListOf<Pair<AfterpartySummary, SettlementItem>>()
            for (party in afterparties) {
                when (val result = fetchAllPages({ item -> item.id }) { settlements.fetchSettlements(party.id, it) }) {
                    is YappResult.Failure ->
                        if (result.reason !in unavailableParties) return result
                    is YappResult.Success -> rows.addAll(result.value.map { party to it })
                }
            }
            return YappResult.Success(rows)
        }
    }

private val unavailableParties = setOf(AfterpartyFailure.NotFound, AfterpartyFailure.Forbidden)
