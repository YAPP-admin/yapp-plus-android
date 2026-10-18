package com.yapp.plus.feature.main.schedule.model

import com.yapp.plus.domain.session.CurrentGenerationSchedule
import com.yapp.plus.domain.session.ScheduledSession
import com.yapp.plus.domain.session.SessionType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SessionScheduleUiMapperTest {
    @Test
    fun emptyGenerationScheduleReturnsEmptyState() {
        val schedule =
            CurrentGenerationSchedule(
                generationId = 19,
                generationNumber = 19,
                sessions = emptyList(),
            )

        assertEquals(ScheduleUiState.Empty, SessionScheduleUiMapper.toUiState(schedule))
    }

    @Test
    fun scheduleStatusUsesSessionDateAndEndTime() {
        val now = LocalDateTime.of(2026, 10, 18, 12, 0)
        val schedule =
            CurrentGenerationSchedule(
                generationId = 19,
                generationNumber = 19,
                sessions =
                    listOf(
                        session(
                            date = LocalDate.of(2026, 10, 17),
                            endTime = LocalTime.of(18, 0),
                        ),
                        session(
                            date = LocalDate.of(2026, 10, 18),
                            endTime = LocalTime.of(11, 59),
                        ),
                        session(
                            date = LocalDate.of(2026, 10, 18),
                            endTime = LocalTime.of(18, 0),
                        ),
                        session(
                            date = LocalDate.of(2026, 10, 19),
                            endTime = LocalTime.of(18, 0),
                        ),
                    ),
            )

        val state = SessionScheduleUiMapper.toUiState(schedule, now)

        assertEquals(
            listOf(
                ScheduleStatusUiModel.Past,
                ScheduleStatusUiModel.Past,
                ScheduleStatusUiModel.Today,
                ScheduleStatusUiModel.Upcoming,
            ),
            (state as ScheduleUiState.Content).value.map { it.status },
        )
    }

    private fun session(
        date: LocalDate,
        endTime: LocalTime,
    ) = ScheduledSession(
        sessionId = 1,
        name = "세션",
        date = date,
        dayOfWeek = date.dayOfWeek,
        location = null,
        startTime = LocalTime.of(14, 0),
        endTime = endTime,
        type = SessionType.REGULAR,
    )
}
