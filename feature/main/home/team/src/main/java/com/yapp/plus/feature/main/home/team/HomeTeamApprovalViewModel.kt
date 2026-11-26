package com.yapp.plus.feature.main.home.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val ACKNOWLEDGED_TEAM_APPROVAL_SESSIONS = "acknowledgedTeamApprovalSessions"

@HiltViewModel
internal class HomeTeamApprovalViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val acknowledgedSessions =
            savedStateHandle.getStateFlow(ACKNOWLEDGED_TEAM_APPROVAL_SESSIONS, LongArray(0))

        fun acknowledge(sessionId: Long) {
            val sessions = acknowledgedSessions.value
            if (sessionId in sessions) return
            savedStateHandle[ACKNOWLEDGED_TEAM_APPROVAL_SESSIONS] = sessions + sessionId
        }
    }
