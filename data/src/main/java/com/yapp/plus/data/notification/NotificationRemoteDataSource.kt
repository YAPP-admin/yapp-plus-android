package com.yapp.plus.data.notification

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NotificationRemoteDataSource(
    private val client: HttpClient,
) : NotificationDataSource {
    override suspend fun fetchNotifications(cursor: String?): NotificationPageData =
        client
            .get("users/me/notifications") {
                cursor?.let { parameter("cursor", it) }
            }.body<NotificationPageResponse>()
            .toData()
}
