package com.nikol.calendar.domain.repo

import arrow.core.raise.Raise
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ScheduleRepository {

    fun observeEvents(
        start: Instant,
        end: Instant
    ): Flow<List<CalendarEvent>>

    context(raise: Raise<ScheduleError>)
    suspend fun getEvent(
        href: String,
        start: Instant
    ): CalendarEvent

    context(raise: Raise<ScheduleError>)
    suspend fun refresh()
}