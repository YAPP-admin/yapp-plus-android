package com.yapp.plus.core.ui.mapper

import com.yapp.plus.core.ui.model.AttendanceStatusUiModel
import com.yapp.plus.domain.session.AttendanceStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceStatusMapperTest {
    @Test
    fun mapsAttendanceStatusToAttendanceStatusUiModelCorrectly() {
        assertEquals(
            AttendanceStatusUiModel.NotEligible,
            AttendanceStatus.NOT_ELIGIBLE.toUiModel(),
        )
        assertEquals(
            AttendanceStatusUiModel.NotOpen,
            AttendanceStatus.NOT_OPEN.toUiModel(),
        )
        assertEquals(
            AttendanceStatusUiModel.Available,
            AttendanceStatus.AVAILABLE.toUiModel(),
        )
        assertEquals(
            AttendanceStatusUiModel.CheckedIn,
            AttendanceStatus.CHECKED_IN.toUiModel(),
        )
        assertEquals(
            AttendanceStatusUiModel.PendingApproval,
            AttendanceStatus.PENDING_APPROVAL.toUiModel(),
        )
    }
}
