package com.yapp.plus

import android.app.Application
import com.yapp.plus.notification.createPushChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YappApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createPushChannel(this)
    }
}
