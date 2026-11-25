package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.AfterpartyResult
import com.yapp.plus.domain.afterparty.SettlementDetail
import com.yapp.plus.domain.afterparty.SettlementPaymentStatus
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

        fun updatePayment(
            settlementId: Long,
            status: SettlementPaymentStatus,
        ) {
            val current = state.value
            if (current.submittingSettlementId != null) return
            val detail =
                current.payments.valueOrNull()?.firstOrNull { it.item.id == settlementId }
                    ?: current.detail.valueOrNull()?.takeIf { it.item.id == settlementId }
                    ?: return
            val payment = detail.item.paymentStatus
            if (detail.item.isCreator || payment == null || payment == status) return

            operation?.cancel()
            mutableState.update {
                it.copy(submittingSettlementId = settlementId, failure = null, paymentCompleted = false)
            }
            operation = viewModelScope.launch { submitPayment(detail, status) }
        }

        fun dismissPaymentNotice() {
            mutableState.update { it.copy(paymentCompleted = false) }
        }

        private suspend fun submitPayment(
            detail: SettlementDetail,
            status: SettlementPaymentStatus,
        ) {
            val item = detail.item
            try {
                when (val result = repository.updatePayment(item.afterpartyId, item.id, status)) {
                    is YappResult.Failure -> mutableState.update { it.copy(failure = result.reason) }
                    is YappResult.Success -> updatePaymentResult(detail, result.value)
                }
            } finally {
                mutableState.update { it.copy(submittingSettlementId = null) }
            }
        }

        private fun updatePaymentResult(
            detail: SettlementDetail,
            status: SettlementPaymentStatus,
        ) {
            val changed = detail.copy(item = detail.item.copy(paymentStatus = status))
            mutableState.update { current ->
                current.copy(
                    detail =
                        if (current.detail
                                .valueOrNull()
                                ?.item
                                ?.id == detail.item.id
                        ) {
                            YappResult.Success(changed)
                        } else {
                            current.detail
                        },
                    payments =
                        current.payments.valueOrNull()?.let { payments ->
                            YappResult.Success(payments.map { if (it.item.id == detail.item.id) changed else it })
                        } ?: current.payments,
                    paymentCompleted = status == SettlementPaymentStatus.PAID,
                )
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
            val detail = result.valueOrNull()
            if (detail != null) {
                if (detail.item.isCreator) {
                    loadPeople(detail, state.value.filter)
                    val summary = repository.fetchSummary(afterpartyId, settlementId)
                    mutableState.update { it.copy(summary = summary) }
                } else {
                    val payments = loadPayments(detail)
                    mutableState.update { it.copy(payments = payments) }
                }
            }
            val event = party.await()
            mutableState.update { it.copy(afterparty = event) }
        }

        private suspend fun loadPayments(anchor: SettlementDetail): AfterpartyResult<List<SettlementDetail>> =
            coroutineScope {
                val afterpartyId = anchor.item.afterpartyId
                val result =
                    fetchAllPages({ item -> item.id }) { cursor ->
                        repository.fetchSettlements(afterpartyId, cursor)
                    }
                val items =
                    when (result) {
                        is YappResult.Failure -> return@coroutineScope result
                        is YappResult.Success -> result.value.filter { !it.isCreator && it.paymentStatus != null }
                    }
                val details = mutableListOf<SettlementDetail>()
                for (batch in items.chunked(4)) {
                    val results =
                        batch
                            .map { item ->
                                async {
                                    if (item.id == anchor.item.id) {
                                        YappResult.Success(anchor)
                                    } else {
                                        repository.fetchSettlement(afterpartyId, item.id)
                                    }
                                }
                            }.awaitAll()
                    for (detail in results) {
                        when (detail) {
                            is YappResult.Failure -> {
                                if (detail.reason !in inaccessiblePayments) return@coroutineScope detail
                            }
                            is YappResult.Success -> {
                                if (!detail.value.item.isCreator && detail.value.item.paymentStatus != null) {
                                    details.add(detail.value)
                                }
                            }
                        }
                    }
                }
                if (details.none { it.item.id == anchor.item.id } && anchor.item.paymentStatus != null) {
                    details.add(0, anchor)
                }
                YappResult.Success(details)
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

private val inaccessiblePayments = setOf(AfterpartyFailure.NotFound, AfterpartyFailure.Forbidden)
