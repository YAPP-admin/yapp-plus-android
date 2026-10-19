package com.yapp.plus.core.network

import android.util.Log
import io.ktor.client.plugins.logging.Logger

internal object AndroidNetworkLogger : Logger {
    override fun log(message: String) {
        Log.d(TAG, message)
    }

    private const val TAG = "YappNetwork"
}
