package com.nikol.calendar.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "calendar_events",
    indices = [
        Index("firstStart"),
        Index("recurrenceUntil")
    ]
)
data class CalendarEventEntity(
    @PrimaryKey
    val href: String,
    val uid: String,

    val title: String?,
    val description: String?,
    val location: String?,
    val timeZoneId: String?,

    // DTSTART / DTEND первого экземпляра
    val firstStart: Instant,
    val firstEnd: Instant,

    // RRULE
    val recurrenceRule: String?,

    // UNTIL из RRULE
    val recurrenceUntil: Instant?,

    // Список отменённых и переопределённых дат (EXDATE + RECURRENCE-ID)
    val exdates: List<Instant> = emptyList(),

    val rawIcs: String,
    val eTag: String
)