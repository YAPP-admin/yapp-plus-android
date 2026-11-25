package com.yapp.plus.feature.main.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.afterparty.AfterpartyFailure
import com.yapp.plus.domain.afterparty.SettlementDraft
import com.yapp.plus.domain.afterparty.SettlementRepository
import com.yapp.plus.domain.result.YappResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettlementEditorViewModel
    @Inject
    constructor(
        private val repository: SettlementRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(SettlementEditorState())
        val state = mutableState.asStateFlow()
        private var opened = false

        fun load(
            afterpartyId: Long,
            settlementId: Long?,
        ) {
            if (opened) return
            opened = true
            viewModelScope.launch {
                val current =
                    if (settlementId == null) {
                        null
                    } else {
                        when (val result = repository.fetchEdit(afterpartyId, settlementId)) {
                            is YappResult.Failure -> {
                                failLoading(result.reason)
                                return@launch
                            }
                            is YappResult.Success -> result.value
                        }
                    }
                val candidates =
                    fetchAllPages({ person -> person.recipientId }) { cursor ->
                        repository.fetchCandidates(afterpartyId, settlementId, cursor)
                    }
                when (candidates) {
                    is YappResult.Failure -> failLoading(candidates.reason)
                    is YappResult.Success -> {
                        val people = candidates.value.filterNot { it.isMe }
                        mutableState.value =
                            SettlementEditorState(
                                candidates = people,
                                selected = current?.recipientIds ?: people.map { it.recipientId }.toSet(),
                                bank = current?.account?.bank,
                                number = current?.account?.number.orEmpty(),
                                holder = current?.account?.holder.orEmpty(),
                                totalText = current?.totalAmount?.toString().orEmpty(),
                                amountOverride = current?.perPersonAmount?.toString(),
                                photos = current?.photos.orEmpty(),
                                revision = current?.revision,
                                financialLocked = (current?.paidCount ?: 0) > 0,
                                loading = false,
                            )
                    }
                }
            }
        }

        fun edit(transform: (SettlementEditorState) -> SettlementEditorState) {
            mutableState.update {
                if (it.loading || it.saving || it.blocked || it.financialLocked) {
                    it
                } else {
                    transform(it).copy(failure = null)
                }
            }
        }

        fun removePhoto(id: Long) {
            mutableState.update {
                if (it.saving || it.blocked) it else it.copy(photos = it.photos.filterNot { photo -> photo.id == id })
            }
        }

        fun save(
            afterpartyId: Long,
            settlementId: Long?,
            readPhotos: List<suspend () -> SettlementPhotoInput?>,
        ) {
            val current = state.value
            val draft = current.draft() ?: return
            if (current.loading || current.saving || current.blocked || current.savedId != null) return
            if (draft.imageIds.size + readPhotos.size > SettlementDraft.MAX_PHOTOS) return
            mutableState.update { it.copy(saving = true, failure = null) }
            viewModelScope.launch {
                try {
                    val ids = uploadPhotos(afterpartyId, settlementId, draft.imageIds, readPhotos) ?: return@launch
                    val request = draft.copy(imageIds = ids)
                    val result =
                        if (settlementId == null) {
                            repository.createSettlement(afterpartyId, request)
                        } else {
                            repository.updateSettlement(
                                afterpartyId,
                                settlementId,
                                checkNotNull(current.revision),
                                request,
                            )
                        }
                    when (result) {
                        is YappResult.Success -> mutableState.update { it.copy(savedId = result.value.item.id) }
                        is YappResult.Failure -> showSubmissionFailure(result.reason, settlementId == null)
                    }
                } finally {
                    mutableState.update { it.copy(saving = false) }
                }
            }
        }

        private suspend fun uploadPhotos(
            afterpartyId: Long,
            settlementId: Long?,
            existingIds: List<Long>,
            readers: List<suspend () -> SettlementPhotoInput?>,
        ): List<Long>? {
            val ids = existingIds.toMutableList()
            for (read in readers) {
                val photo = read()
                if (photo == null) {
                    mutableState.update { it.copy(failure = AfterpartyFailure.InvalidRequest) }
                    return null
                }
                val result =
                    repository.uploadPhoto(
                        afterpartyId,
                        settlementId,
                        photo.filename,
                        photo.contentType,
                        photo.bytes,
                    )
                when (result) {
                    is YappResult.Success -> ids.add(result.value)
                    is YappResult.Failure -> {
                        mutableState.update { it.copy(failure = result.reason) }
                        return null
                    }
                }
            }
            return ids
        }

        private fun showSubmissionFailure(
            failure: AfterpartyFailure,
            creating: Boolean,
        ) {
            val blocked =
                failure == AfterpartyFailure.RevisionConflict ||
                    failure == AfterpartyFailure.FinancialEditLocked ||
                    (creating && failure == AfterpartyFailure.Unavailable)
            mutableState.update { it.copy(failure = failure, blocked = blocked) }
        }

        private fun failLoading(failure: AfterpartyFailure) {
            mutableState.update { it.copy(loading = false, failure = failure, blocked = true) }
        }
    }
