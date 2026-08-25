package com.nikol.calendar.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

    // список отменённых и переопределённых дат (EXDATE + RECURRENCE-ID)
    val exdates: List<Instant> = emptyList(),
    val overrides: List<OverrideEventDto> = emptyList(),

    val rawIcs: String,
    val eTag: String
)

@Serializable
data class OverrideEventDto(
    val start: kotlin.time.Instant,
    val end: kotlin.time.Instant,
    val title: String? = null,
    val description: String? = null
)

class JsonConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromOverrideList(value: List<OverrideEventDto>?): String {
        if (value.isNullOrEmpty()) return "[]"
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toOverrideList(value: String?): List<OverrideEventDto> {
        if (value.isNullOrBlank() || value == "[]") return emptyList()
        return runCatching { json.decodeFromString<List<OverrideEventDto>>(value) }.getOrDefault(emptyList())
    }
}