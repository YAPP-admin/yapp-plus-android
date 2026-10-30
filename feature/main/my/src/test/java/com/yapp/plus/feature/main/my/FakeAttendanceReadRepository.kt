package com.yapp.plus.feature.main.my

import com.yapp.plus.domain.attendance.AttendanceHistoryPage
import com.yapp.plus.domain.attendance.AttendanceReadRepository
import com.yapp.plus.domain.attendance.AttendanceScore
import kotlinx.coroutines.CompletableDeferred

internal class FakeAttendanceReadRepository : AttendanceReadRepository {
    var score: AttendanceScore? = AttendanceScore(1, 86, 3, 1, 0, 1)
    var scoreError: Exception? = null
    var historyError: Exception? = null
    var gate: CompletableDeferred<Unit>? = null
    var scoreRequests = 0
    val historyCursors = mutableListOf<String?>()
    val pages = mutableMapOf<String?, AttendanceHistoryPage>()

    override suspend fun fetchScore(): AttendanceScore? {
        scoreRequests++
        gate?.await()
        scoreError?.let { throw it }
        return score
    }

    override suspend fun fetchHistory(cursor: String?): AttendanceHistoryPage {
        historyCursors.add(cursor)
        gate?.await()
        historyError?.let { throw it }
        return pages[cursor] ?: AttendanceHistoryPage(0, 0, emptyList(), null)
    }
}
