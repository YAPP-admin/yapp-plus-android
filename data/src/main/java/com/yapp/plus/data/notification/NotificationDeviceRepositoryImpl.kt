package com.yapp.plus.data.notification

import com.yapp.plus.domain.notification.NotificationDeviceRepository

class NotificationDeviceRepositoryImpl(
    private val source: NotificationDeviceDataSource,
) : NotificationDeviceRepository {
    override suspend fun register(
        installationId: String,
        registrationToken: String,
    ) {
        requireValidInstallationId(installationId)
        require(registrationToken.encodeToByteArray().size in 1..4096)
        require(registrationToken.none { it.isWhitespace() || it.isISOControl() })
        source.register(installationId, registrationToken)
    }

    override suspend fun unregister(installationId: String) {
        requireValidInstallationId(installationId)
        source.unregister(installationId)
    }

    private fun requireValidInstallationId(value: String) {
        require(value.matches(Regex("[A-Za-z0-9_-]{1,128}")))
    }
}
