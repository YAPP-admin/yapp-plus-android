package com.yapp.plus.domain.notification

interface NotificationDeviceRepository {
    suspend fun register(
        installationId: String,
        registrationToken: String,
    )

    suspend fun unregister(installationId: String)
}
