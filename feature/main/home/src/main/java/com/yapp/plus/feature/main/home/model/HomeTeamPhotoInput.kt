package com.yapp.plus.feature.main.home.model

import android.graphics.Bitmap

internal class HomeTeamPhotoInput(
    val bytes: ByteArray,
    val contentType: String,
    val preview: Bitmap?,
) {
    val filename: String
        get() = if (contentType == "image/png") "team-attendance.png" else "team-attendance.jpg"
}
