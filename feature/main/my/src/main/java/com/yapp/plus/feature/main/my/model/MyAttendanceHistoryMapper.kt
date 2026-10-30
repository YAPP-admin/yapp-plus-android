package com.yapp.plus.feature.main.my.model

import com.yapp.plus.domain.attendance.AttendanceHistoryEntry
import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.AttendanceStaffDecision

internal fun AttendanceHistoryPage.toUiState(): MyAttendanceHistoryState.Content =
    MyAttendanceHistoryState.Content(
        summary =
            MySessionSummary(
                totalSessionCount = totalSessionCount,
                remainingSessionCount = totalSessionCount - startedSessionCount,
            ),
        items =
            items.map {
                MyAttendanceHistoryItem(it.sessionId, it.sessionName, it.toUiStatus())
            },
        nextCursor = nextCursor,
    )

private fun AttendanceHistoryEntry.toUiStatus(): MyAttendanceStatus =
    when (status) {
        AttendanceResultStatus.PRESENT -> MyAttendanceStatus.Present
        AttendanceResultStatus.LATE -> MyAttendanceStatus.Late
        AttendanceResultStatus.ABSENT -> MyAttendanceStatus.Absent
        AttendanceResultStatus.EARLY_LEAVE -> MyAttendanceStatus.EarlyLeave
        AttendanceResultStatus.LATE_AND_EARLY_LEAVE -> MyAttendanceStatus.LateAndEarlyLeave
        AttendanceResultStatus.EXCUSED_ABSENCE -> MyAttendanceStatus.ExcusedAbsence
        AttendanceResultStatus.UNDECIDED -> staffDecision.toPendingUiStatus()
    }

private fun AttendanceStaffDecision?.toPendingUiStatus(): MyAttendanceStatus =
    when (this) {
        AttendanceStaffDecision.PENDING -> MyAttendanceStatus.Pending
        AttendanceStaffDecision.REJECTED -> MyAttendanceStatus.Rejected
        AttendanceStaffDecision.NOT_SUBMITTED -> MyAttendanceStatus.NotSubmitted
        AttendanceStaffDecision.NOT_SELECTED -> MyAttendanceStatus.NotSelected
        else -> MyAttendanceStatus.Undecided
    }
