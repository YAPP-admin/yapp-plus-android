package com.yapp.plus.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yapp.plus.domain.result.YappResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : Any, E : Any>(
    initialState: S,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = mutableUiState.asStateFlow()

    private val eventChannel = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = eventChannel.receiveAsFlow()

    protected fun updateState(transform: (S) -> S) {
        mutableUiState.update(transform)
    }

    protected suspend fun sendEvent(event: E) {
        eventChannel.send(event)
    }

    protected fun <T, F : Any> launchRequest(
        request: suspend () -> YappResult<T, F>,
        onStart: (S) -> S,
        onSuccess: (S, T) -> S,
        onFailure: (S, F) -> S,
    ): Job =
        viewModelScope.launch {
            updateState(onStart)
            val result = request()
            ensureActive()
            when (result) {
                is YappResult.Success -> updateState { onSuccess(it, result.value) }
                is YappResult.Failure -> updateState { onFailure(it, result.reason) }
            }
        }
}
