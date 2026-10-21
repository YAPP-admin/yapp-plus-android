package com.yapp.plus.feature.main.my.model

import com.yapp.plus.domain.my.MyAttendanceRecord
import com.yapp.plus.domain.my.MyProfile
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun MyProfile?.toSummaryState(): MyProfileSummaryState =
    this?.let { MyProfileSummaryState.Content(it.displayName) } ?: MyProfileSummaryState.Empty

internal fun List<MyAttendanceRecord>.toAttendanceHistoryState(): MyAttendanceHistoryState =
    if (isEmpty()) {
        MyAttendanceHistoryState.Empty
    } else {
        MyAttendanceHistoryState.Content(
            map { record ->
                MyAttendanceHistoryItem(
                    sessionName = record.sessionName,
                    checkedInAtLabel = record.checkedInAt.atZone(SEOUL_ZONE).format(CHECKED_IN_AT_FORMATTER),
                )
            },
        )
    }

private val SEOUL_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
private val CHECKED_IN_AT_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy.MM.dd (E) a h:mm", Locale.KOREAN)
