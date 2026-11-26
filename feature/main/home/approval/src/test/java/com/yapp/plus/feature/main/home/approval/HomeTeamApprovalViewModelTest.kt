package com.yapp.plus.feature.main.home.approval

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeTeamApprovalViewModelTest {
    @Test
    fun restoresAcknowledgedSessionsAndIgnoresDuplicateConfirmations() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = HomeTeamApprovalViewModel(savedStateHandle)
        viewModel.acknowledge(42L)

        val restored = HomeTeamApprovalViewModel(savedStateHandle)
        assertEquals(listOf(42L), restored.acknowledgedSessions.value.toList())

        restored.acknowledge(43L)
        restored.acknowledge(43L)
        assertEquals(listOf(42L, 43L), restored.acknowledgedSessions.value.toList())
    }
}
