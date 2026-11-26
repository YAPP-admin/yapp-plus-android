package com.yapp.plus.feature.main.home.approval

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@Composable
fun HomeTeamApprovalRoute(
    sessionId: Long?,
    viewModelStoreOwner: ViewModelStoreOwner?,
    content: @Composable (isAcknowledged: Boolean, onAcknowledge: () -> Unit) -> Unit,
) {
    val viewModel: HomeTeamApprovalViewModel =
        hiltViewModel(viewModelStoreOwner ?: checkNotNull(LocalViewModelStoreOwner.current))
    val acknowledgedSessions by viewModel.acknowledgedSessions.collectAsStateWithLifecycle()
    val isAcknowledged = sessionId != null && sessionId in acknowledgedSessions
    content(isAcknowledged) {
        sessionId?.let(viewModel::acknowledge)
    }
}
