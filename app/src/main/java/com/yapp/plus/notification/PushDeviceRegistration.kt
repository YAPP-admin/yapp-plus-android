package com.yapp.plus.notification

internal data class PushDeviceRegistration(
    val sessionKey: String,
    val installationId: String,
    val token: String,
) {
    override fun toString(): String = "PushDeviceRegistration(<redacted>)"
}
