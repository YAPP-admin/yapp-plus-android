package com.yapp.plus.core.designsystem.component.toast

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

private const val TOAST_BUFFER_CAPACITY = 16

class YappToastManager {
    private val mutableToasts =
        MutableSharedFlow<YappToastData>(
            replay = 0,
            extraBufferCapacity = TOAST_BUFFER_CAPACITY,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    val toasts: SharedFlow<YappToastData> = mutableToasts.asSharedFlow()

    suspend fun show(toast: YappToastData) {
        mutableToasts.emit(toast)
    }

    fun tryShow(toast: YappToastData): Boolean = mutableToasts.tryEmit(toast)
}
