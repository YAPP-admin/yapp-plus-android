package com.yapp.plus.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveAsEvents(
    event: Flow<T>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(event, lifecycleOwner, minActiveState) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            event.collect { currentOnEvent(it) }
        }
    }
}
