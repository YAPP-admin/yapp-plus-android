package com.yapp.plus.notification

import com.yapp.plus.domain.notification.NotificationDeviceRepository

internal class FakeNotificationDevices : NotificationDeviceRepository {
    val calls = mutableListOf<String>()
    var register: suspend () -> Unit = {}
    var unregister: suspend () -> Unit = {}

    override suspend fun register(
        installationId: String,
        registrationToken: String,
    ) {
        calls += "PUT $installationId $registrationToken"
        register()
    }

    override suspend fun unregister(installationId: String) {
        calls += "DELETE $installationId"
        unregister()
    }
}
