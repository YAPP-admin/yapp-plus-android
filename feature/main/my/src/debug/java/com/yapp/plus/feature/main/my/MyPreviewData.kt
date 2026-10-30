package com.yapp.plus.feature.main.my

import com.yapp.plus.domain.attendance.AttendanceScore
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryItem
import com.yapp.plus.feature.main.my.model.MyAttendanceHistoryState
import com.yapp.plus.feature.main.my.model.MyAttendanceScoreState
import com.yapp.plus.feature.main.my.model.MyAttendanceStatus
import com.yapp.plus.feature.main.my.model.MyProfileUiModel
import com.yapp.plus.feature.main.my.model.MyProfileUiState
import com.yapp.plus.feature.main.my.model.MySessionSummary
import com.yapp.plus.feature.main.my.model.MyUiState

object MyPreviewData {
    val profile = MyProfileUiModel("김야뿌", "활동회원", "28기", "Designer")
    val score = MyAttendanceScoreState.Content(AttendanceScore(1, 92, 3, 1, 0, 0), 1)
    val history =
        MyAttendanceHistoryState.Content(
            summary = MySessionSummary(12, 6, 50),
            items =
                listOf(
                    MyAttendanceHistoryItem(6, "개발-디자인 세션", MyAttendanceStatus.Absent),
                    MyAttendanceHistoryItem(5, "팀세션", MyAttendanceStatus.Late),
                    MyAttendanceHistoryItem(4, "기획 세션", MyAttendanceStatus.Late),
                    MyAttendanceHistoryItem(3, "친바라 세션", MyAttendanceStatus.Absent),
                    MyAttendanceHistoryItem(2, "팀빌딩", MyAttendanceStatus.Present),
                    MyAttendanceHistoryItem(1, "OT", MyAttendanceStatus.Present),
                ),
        )
    val state =
        MyUiState(
            profile = MyProfileUiState.Content(profile),
            score = score,
            attendanceHistory = history,
        )
}
