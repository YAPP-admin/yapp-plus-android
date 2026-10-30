package com.yapp.plus.data.notification

interface NotificationDeviceDataSource {
    suspend fun register(
        installationId: String,
        registrationToken: String,
    )

    suspend fun unregister(installationId: String)
}
