package com.yapp.plus.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yapp.plus.MainActivity
import com.yapp.plus.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class YappMessagingService : FirebaseMessagingService() {
    @Inject internal lateinit var registration: PushRegistration

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onNewToken(token: String) {
        registration.synchronize()
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        val payload = message.notification ?: return
        if (!registration.isConfigured() || !canShowPushNotifications(this)) return
        val tag = message.notificationTag()
        val notification =
            NotificationCompat
                .Builder(this, getString(R.string.push_notification_channel_id))
                .setSmallIcon(R.drawable.ic_push_notification)
                .setContentTitle(payload.title ?: getString(R.string.app_name))
                .setContentText(payload.body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(payload.body))
                .setAutoCancel(true)
                .setContentIntent(notificationIntent(message, tag))
                .build()
        try {
            NotificationManagerCompat.from(this).notify(tag, 0, notification)
        } catch (_: SecurityException) {
            // The user can revoke notification permission while a message is being delivered.
        }
    }

    private fun notificationIntent(
        message: RemoteMessage,
        tag: String,
    ): PendingIntent {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                data =
                    Uri
                        .Builder()
                        .scheme("yapp-push")
                        .authority("notification")
                        .appendPath(tag)
                        .build()
                if (message.data[PUSH_SOURCE] == PUSH_POST) {
                    putExtra(PUSH_SOURCE, PUSH_POST)
                    putExtra(PUSH_POST_ID, message.data[PUSH_POST_ID])
                }
            }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

private fun RemoteMessage.notificationTag(): String =
    when (data[PUSH_SOURCE]) {
        PUSH_POST -> data[PUSH_POST_ID]?.let { "post-$it" }
        else -> data[PUSH_NOTIFICATION_ID]?.let { "notice-$it" }
    } ?: messageId ?: sentTime.toString()
