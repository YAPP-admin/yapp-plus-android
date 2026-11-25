package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettlementViewModel
    @Inject
    constructor(
        private val repository: SettlementRepository,
        private val parties: AfterpartyRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(SettlementScreenState())
        val state = mutableState.asStateFlow()
        private var operation: Job? = null
        private var peopleLoad: Job? = null

        fun load(
            afterpartyId: Long,
            settlementId: Long,
        ) {
            if (operation?.isActive == true) return
            operation = viewModelScope.launch { fetchDetail(afterpartyId, settlementId) }
        }

        fun selectFilter(filter: SettlementPaymentStatus) {
            val detail =
                state.value.detail
                    .valueOrNull()
                    ?.takeIf { it.item.isCreator } ?: return
            if (state.value.filter == filter) return
            mutableState.update { it.copy(filter = filter, people = null) }
            loadPeople(detail, filter)
        }

        fun updatePayment(status: SettlementPaymentStatus) {
            val detail = state.value.detail.valueOrNull() ?: return
            if (state.value.submitting || detail.item.isCreator || detail.item.paymentStatus == status) return
            operation?.cancel()
            mutableState.update { it.copy(submitting = true, failure = null) }
            operation =
                viewModelScope.launch {
                    try {
                        when (val result = repository.updatePayment(detail.item.afterpartyId, detail.item.id, status)) {
                            is YappResult.Failure -> mutableState.update { it.copy(failure = result.reason) }
                            is YappResult.Success ->
                                mutableState.update {
                                    it.copy(
                                        detail =
                                            YappResult.Success(
                                                detail.copy(item = detail.item.copy(paymentStatus = result.value)),
                                            ),
                                    )
                                }
                        }
                    } finally {
                        mutableState.update { it.copy(submitting = false) }
                    }
                }
        }

        private suspend fun fetchDetail(
            afterpartyId: Long,
            settlementId: Long,
        ) = coroutineScope {
            val party = async { parties.fetchAfterparty(afterpartyId).valueOrNull() }
            val result = repository.fetchSettlement(afterpartyId, settlementId)
            ensureActive()
            mutableState.update { it.copy(detail = result) }
            val detail = result.valueOrNull()?.takeIf { it.item.isCreator }
            if (detail != null) loadPeople(detail, state.value.filter)
            val summary = detail?.let { repository.fetchSummary(afterpartyId, settlementId) }
            val event = party.await()
            mutableState.update { it.copy(summary = summary, afterparty = event) }
        }

        private fun loadPeople(
            detail: SettlementDetail,
            filter: SettlementPaymentStatus,
        ) {
            peopleLoad?.cancel()
            peopleLoad =
                viewModelScope.launch {
                    val result =
                        fetchAllPages({ person -> person.recipientId }) { cursor ->
                            repository.fetchRecipients(detail.item.afterpartyId, detail.item.id, filter, cursor)
                        }
                    ensureActive()
                    mutableState.update { it.copy(people = result) }
                }
        }
    }
