package com.yapp.plus.core.ui.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.yapp.plus.core.ui.model.HomeAttendanceLocationIssue

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

fun Context.openAttendanceLocationSettings(issue: HomeAttendanceLocationIssue): Boolean {
    val intent =
        when (issue) {
            HomeAttendanceLocationIssue.LocationDisabled ->
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            HomeAttendanceLocationIssue.PermissionDenied,
            HomeAttendanceLocationIssue.PrecisePermissionRequired,
            ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData("package:$packageName".toUri())
            else -> return false
        }
    return runCatching { startActivity(intent) }.isSuccess
}
