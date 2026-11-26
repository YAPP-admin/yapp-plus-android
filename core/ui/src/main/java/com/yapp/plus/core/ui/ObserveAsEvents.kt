package com.yapp.plus.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Lifecycle 상태가 [minActiveState] 이상일 때 [event]의 일회성 이벤트를 수집합니다.
 *
 * @param event 관찰할 이벤트 흐름.
 * @param minActiveState 수집을 시작하는 최소 Lifecycle 상태.
 * @param onEvent 이벤트가 발생했을 때 실행할 suspend 콜백 함수.
 */
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
