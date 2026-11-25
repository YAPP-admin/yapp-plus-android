package com.yapp.plus.feature.main.my.model

import com.yapp.plus.domain.attendance.AttendanceHistoryEntry
import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceResultStatus
import com.yapp.plus.domain.attendance.AttendanceStaffDecision

internal object MyAttendanceHistoryMapper {
    fun toUiState(page: AttendanceHistoryPage): MyAttendanceHistoryState.Content =
        MyAttendanceHistoryState.Content(
            summary =
                MySessionSummary(
                    totalSessionCount = page.totalSessionCount,
                    remainingSessionCount = page.totalSessionCount - page.startedSessionCount,
                ),
            items =
                page.items.map {
                    MyAttendanceHistoryItem(it.sessionId, it.sessionName, it.toUiStatus())
                },
            nextCursor = page.nextCursor,
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
}

internal fun AttendanceHistoryPage.toUiState(): MyAttendanceHistoryState.Content =
    MyAttendanceHistoryMapper.toUiState(this)
