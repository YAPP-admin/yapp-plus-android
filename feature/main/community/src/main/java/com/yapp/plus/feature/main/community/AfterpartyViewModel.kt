package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.AfterpartyRepository
import com.yapp.plus.domain.afterparty.AfterpartyResponse
import com.yapp.plus.domain.afterparty.AfterpartyState
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
internal class AfterpartyViewModel
    @Inject
    constructor(
        private val parties: AfterpartyRepository,
        private val settlements: SettlementRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(AfterpartyScreenState())
        val state = mutableState.asStateFlow()
        private var operation: Job? = null

        fun load(id: Long) {
            if (operation?.isActive == true) return
            operation = viewModelScope.launch { fetchDetail(id) }
        }

        fun vote(
            response: AfterpartyResponse,
            now: Instant = Instant.now(),
        ) {
            val detail = state.value.detail.valueOrNull() ?: return
            if (state.value.submitting || response == AfterpartyResponse.UNANSWERED) return
            if (!detail.canVote(now)) {
                mutableState.update { it.copy(failure = AfterpartyFailure.VotingClosed) }
                load(detail.id)
                return
            }
            operation?.cancel()
            mutableState.update { it.copy(submitting = true, failure = null) }
            operation =
                viewModelScope.launch {
                    try {
                        when (val result = parties.updateResponse(detail.id, response)) {
                            is YappResult.Success -> {
                                mutableState.update {
                                    it.copy(
                                        detail = YappResult.Success(detail.copy(response = result.value)),
                                    )
                                }
                                fetchDetail(detail.id)
                            }
                            is YappResult.Failure -> {
                                mutableState.update { it.copy(failure = result.reason) }
                                fetchDetail(detail.id)
                            }
                        }
                    } finally {
                        mutableState.update { it.copy(submitting = false) }
                    }
                }
        }

        private suspend fun fetchDetail(id: Long) {
            val result = parties.fetchAfterparty(id)
            currentCoroutineContext().ensureActive()
            val previous = state.value.detail.valueOrNull()
            if (previous?.id == id && result == YappResult.Failure(AfterpartyFailure.Unavailable)) {
                mutableState.update { it.copy(failure = it.failure ?: AfterpartyFailure.Unavailable) }
                return
            }
            mutableState.update { it.copy(detail = result) }
            val detail = result.valueOrNull() ?: return
            val showSettlementOnly =
                detail.state == AfterpartyState.ENDED && detail.response == AfterpartyResponse.ATTENDING
            val canShowAttendees = detail.response != AfterpartyResponse.UNANSWERED && !showSettlementOnly
            if (!canShowAttendees) {
                mutableState.update { it.copy(attendees = null) }
            }
            coroutineScope {
                val roster =
                    async {
                        if (canShowAttendees) {
                            val people =
                                fetchAllPages({ member -> member.recipientId }) { cursor ->
                                    parties.fetchAttendees(id, cursor)
                                }
                            ensureActive()
                            mutableState.update { it.copy(attendees = people) }
                        }
                    }
                val rows = fetchAllPages({ item -> item.id }) { cursor -> settlements.fetchSettlements(id, cursor) }
                ensureActive()
                mutableState.update { it.copy(settlements = rows) }
                val summaries = settlements.fetchOwnerSummaries(id, rows.valueOrNull().orEmpty())
                ensureActive()
                mutableState.update { it.copy(ownerSummaries = summaries) }
                roster.await()
            }
        }
    }
