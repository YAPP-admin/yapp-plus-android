package com.yapp.plus.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yapp.plus.R

internal const val PUSH_SOURCE = "source"
internal const val PUSH_POST = "POST"
internal const val PUSH_POST_ID = "postId"
internal const val PUSH_NOTIFICATION_ID = "notificationId"

internal fun createPushChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NotificationManager::class.java)
    val channel =
        NotificationChannel(
            context.getString(R.string.push_notification_channel_id),
            context.getString(R.string.push_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
    manager.createNotificationChannel(channel)
}

internal fun canShowPushNotifications(context: Context): Boolean {
    if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }
    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return true
    val manager = context.getSystemService(NotificationManager::class.java)
    val channel = manager.getNotificationChannel(context.getString(R.string.push_notification_channel_id))
    return channel?.importance != NotificationManager.IMPORTANCE_NONE
}

internal fun Intent.pushPostId(): Long? {
    if (getStringExtra(PUSH_SOURCE) != PUSH_POST) return null
    return getStringExtra(PUSH_POST_ID)?.toLongOrNull()?.takeIf { it > 0 }
}
