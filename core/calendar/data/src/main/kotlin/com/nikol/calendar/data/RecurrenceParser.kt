package com.nikol.calendar.data

import com.nikol.calendar.data.local.CalendarEventEntity
import com.nikol.calendar.domain.model.CalendarEvent
import java.time.Instant

interface RecurrenceParser {
    fun expand(
        entity: CalendarEventEntity,
        from: Instant,
        to: Instant
    ): List<CalendarEvent>

    fun parse(
        href: String,
        eTag: String,
        rawIcs: String
    ): CalendarEventEntity
}
