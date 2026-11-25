package com.yapp.plus.feature.main.home.attendance.model

import androidx.annotation.StringRes
import com.yapp.plus.feature.main.home.attendance.R

enum class HomeAttendanceLocationIssue(
    @param:StringRes val messageResource: Int,
    @param:StringRes val actionResource: Int? = null,
) {
    SessionUnavailable(R.string.home_code_location_missing, R.string.home_code_refresh_session),
    PermissionDenied(R.string.home_code_location_permission, R.string.home_code_open_app_settings),
    PrecisePermissionRequired(
        R.string.home_code_location_precise,
        R.string.home_code_open_app_settings,
    ),
    LocationDisabled(R.string.home_distance_disabled, R.string.home_distance_open_settings),
    TimedOut(R.string.home_code_location_timeout),
    Unavailable(R.string.home_distance_unavailable),
    InsufficientAccuracy(R.string.home_code_location_accuracy),
    OutOfRange(R.string.home_code_location_out_of_range),
    SettingsUnavailable(R.string.home_code_location_settings_unavailable),
}
