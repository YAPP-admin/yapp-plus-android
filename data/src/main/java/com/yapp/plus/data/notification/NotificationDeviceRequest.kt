package com.yapp.plus.data.notification

import kotlinx.serialization.Serializable

@Serializable
internal class NotificationDeviceRequest(
    val registrationToken: String,
    val platform: String,
)
