package com.yapp.plus.feature.main.home.model

import android.graphics.Bitmap
import com.yapp.plus.domain.attendance.TeamAttendancePhoto
import com.yapp.plus.domain.attendance.TeamAttendanceRoster

internal data class HomeTeamAttendanceUiState(
    val sessionId: Long? = null,
    val latest: AttendanceUiState<TeamAttendancePhoto?> = AttendanceUiState.Loading,
    val isAbsent: Boolean = false,
    val guide: Boolean = false,
    val preparing: Boolean = false,
    val input: HomeTeamPhotoInput? = null,
    val roster: TeamAttendanceRoster? = null,
    val selected: Set<Long> = emptySet(),
    val isSubmitting: Boolean = false,
    val error: Int? = null,
    val uncertain: Boolean = false,
    val receipt: HomeTeamPhotoReceiptUiModel? = null,
    val photoViewer: HomeTeamPhotoViewerUiState? = null,
) {
    val isActive: Boolean
        get() = guide || preparing || input != null || receipt != null || photoViewer != null

    val photo: TeamAttendancePhoto?
        get() = (latest as? AttendanceUiState.Content)?.value
}

internal class HomeTeamPhotoInput(
    val bytes: ByteArray,
    val contentType: String,
    val preview: Bitmap?,
) {
    val filename: String
        get() = if (contentType == "image/png") "team-attendance.png" else "team-attendance.jpg"
}
