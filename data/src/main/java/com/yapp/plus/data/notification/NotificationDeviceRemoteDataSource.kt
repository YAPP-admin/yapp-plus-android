package com.yapp.plus.data.notification

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class NotificationDeviceRemoteDataSource(
    private val client: HttpClient,
) : NotificationDeviceDataSource {
    override suspend fun register(
        installationId: String,
        registrationToken: String,
    ) {
        val response =
            client.put("users/me/notification-devices/$installationId") {
                contentType(ContentType.Application.Json)
                setBody(NotificationDeviceRequest(registrationToken, "ANDROID"))
            }
        check(response.status == HttpStatusCode.NoContent)
    }

    override suspend fun unregister(installationId: String) {
        check(
            client.delete("users/me/notification-devices/$installationId").status ==
                HttpStatusCode.NoContent,
        )
    }
}
