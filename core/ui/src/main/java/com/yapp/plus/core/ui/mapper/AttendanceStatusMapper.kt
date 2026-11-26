package com.yapp.plus.core.ui.mapper

import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.domain.session.AttendanceStatus

fun AttendanceStatus.toUiModel(): AttendanceStatusUiModel =
    when (this) {
        AttendanceStatus.NOT_ELIGIBLE -> AttendanceStatusUiModel.NotEligible
        AttendanceStatus.NOT_OPEN -> AttendanceStatusUiModel.NotOpen
        AttendanceStatus.AVAILABLE -> AttendanceStatusUiModel.Available
        AttendanceStatus.CHECKED_IN -> AttendanceStatusUiModel.CheckedIn
        AttendanceStatus.PENDING_APPROVAL -> AttendanceStatusUiModel.PendingApproval
    }
