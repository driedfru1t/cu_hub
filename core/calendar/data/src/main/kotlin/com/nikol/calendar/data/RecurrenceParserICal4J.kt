package com.nikol.calendar.data

import com.nikol.calendar.data.local.CalendarEventEntity
import com.nikol.calendar.data.local.OverrideEventDto
import com.nikol.calendar.domain.model.CalendarEvent
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.TzId
import net.fortuna.ical4j.model.property.DateProperty
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.ExDate
import net.fortuna.ical4j.model.property.Location
import net.fortuna.ical4j.model.property.RRule
import net.fortuna.ical4j.model.property.RecurrenceId
import net.fortuna.ical4j.model.property.Summary
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import java.time.zone.ZoneRules
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant


class AndroidTimeZoneRegistry : TimeZoneRegistry {

    private val timeZoneMap = ConcurrentHashMap<String, TimeZone>()
    private val zoneIdMap = ConcurrentHashMap<String, ZoneId>()
    private val zoneRulesMap = ConcurrentHashMap<String, ZoneRules>()

    override fun register(timezone: TimeZone) {
        val id = timezone.id
        if (id != null) {
            timeZoneMap[id] = timezone
        }
    }

    override fun register(timezone: TimeZone, update: Boolean) {
        register(timezone)
    }

    override fun clear() {
        timeZoneMap.clear()
        zoneIdMap.clear()
        zoneRulesMap.clear()
    }

    override fun getTimeZone(id: String?): TimeZone? {
        if (id == null) return null
        return timeZoneMap[id]
    }

    override fun getZoneRules(): Map<String, ZoneRules> {
        return zoneRulesMap
    }

    override fun getZoneId(tzId: String?): ZoneId? {
        if (tzId == null) return null
        return zoneIdMap[tzId]
            ?: runCatching { ZoneId.of(tzId, TimeZoneRegistry.ZONE_ALIASES) }.getOrNull()
            ?: runCatching { ZoneId.of(tzId) }.getOrNull()
            ?: ZoneId.systemDefault()
    }

    override fun getTzId(zoneId: String?): String? {
        if (zoneId == null) return null
        return TimeZoneRegistry.ZONE_IDS[zoneId] ?: zoneId
    }
}


fun createAndroidCalendarBuilder(): CalendarBuilder {
    return CalendarBuilder(
        AndroidTimeZoneRegistry()
    )
}

class RecurrenceParserICal4J @Inject constructor() : RecurrenceParser {
    override fun expand(
        entity: CalendarEventEntity,
        from: Instant,
        to: Instant
    ): List<CalendarEvent> {
        val zoneId = entity.timeZoneId?.let { runCatching { ZoneId.of(it) }.getOrNull() }
            ?: ZoneOffset.UTC

        val resultEvents = mutableListOf<CalendarEvent>()
        val exdateSet: Set<Instant> = entity.exdates.toSet()

        if (entity.recurrenceRule != null) {
            val duration = Duration.between(entity.firstStart, entity.firstEnd)
            val seed = entity.firstStart.atZone(zoneId)
            val rangeStart = from.atZone(zoneId)
            val rangeEnd = to.atZone(zoneId)

            val recur = Recur<ZonedDateTime>(entity.recurrenceRule)
            val occurrenceStarts: List<ZonedDateTime> = recur.getDates(seed, rangeStart, rangeEnd)

            for (startZdt in occurrenceStarts) {
                val startInstant = startZdt.toInstant()

                if (startInstant in exdateSet) continue

                val endInstant = startInstant.plus(duration)

                resultEvents.add(
                    CalendarEvent(
                        id = entity.uid,
                        title = entity.title ?: "",
                        description = entity.description,
                        start = startInstant,
                        end = endInstant
                    )
                )
            }
        } else {
            if (entity.exdates.isEmpty() && entity.firstStart < to && entity.firstEnd > from) {
                resultEvents.add(
                    CalendarEvent(
                        id = entity.uid,
                        title = entity.title ?: "",
                        description = entity.description,
                        start = entity.firstStart,
                        end = entity.firstEnd
                    )
                )
            }
        }

        for (overrideEvent in entity.overrides) {
            if (overrideEvent.start.toJavaInstant() < to && overrideEvent.end.toJavaInstant() > from) {
                resultEvents.add(
                    CalendarEvent(
                        id = entity.uid,
                        title = overrideEvent.title ?: entity.title ?: "",
                        description = overrideEvent.description ?: entity.description,
                        start = overrideEvent.start.toJavaInstant(),
                        end = overrideEvent.end.toJavaInstant()
                    )
                )
            }
        }

        return resultEvents
    }

    override fun parse(
        href: String,
        eTag: String,
        rawIcs: String
    ): CalendarEventEntity {
        val calendar = createAndroidCalendarBuilder().build(rawIcs.reader())
        val events = calendar.getComponents<VEvent>(Component.VEVENT)

        val masterEvent = events.firstOrNull { e ->
            e.getProperty<RRule<*>>(Property.RRULE).isPresent ||
                    e.getProperty<RecurrenceId<*>>(Property.RECURRENCE_ID).isEmpty
        } ?: events.first()

        val uid = masterEvent.uid.orElseThrow().value
        val title = masterEvent.getProperty<Summary>(Property.SUMMARY).getOrNull()?.value
        val description =
            masterEvent.getProperty<Description>(Property.DESCRIPTION).getOrNull()?.value
        val location = masterEvent.getProperty<Location>(Property.LOCATION).getOrNull()?.value

        val allStarts = events.mapNotNull { e ->
            e.getProperty<DtStart<*>>(Property.DTSTART).getOrNull()?.toInstant()
        }
        val allEnds = events.mapNotNull { e ->
            e.getProperty<DtEnd<*>>(Property.DTEND).getOrNull()?.toInstant()
        }

        val dtStart = allStarts.minOrNull() ?: masterEvent.getProperty<DtStart<*>>(Property.DTSTART)
            .orElseThrow().toInstant()
        val dtEnd =
            allEnds.maxOrNull() ?: masterEvent.getProperty<DtEnd<*>>(Property.DTEND).orElseThrow()
                .toInstant()

        val dtStartProp = masterEvent.getProperty<DtStart<*>>(Property.DTSTART).orElseThrow()
        val timeZoneId = dtStartProp.getParameter<TzId>("TZID").getOrNull()?.value
            ?: calendar.getComponents<net.fortuna.ical4j.model.component.VTimeZone>(Component.VTIMEZONE)
                .firstOrNull()?.timeZoneId?.value

        val rruleProp = masterEvent.getProperty<RRule<*>>(Property.RRULE).getOrNull()
        val recurrenceRule = rruleProp?.value
        val rawUntil = rruleProp?.recur?.until?.toInstant()
        val recurrenceUntil = if (rawUntil != null && dtEnd > rawUntil) dtEnd else rawUntil

        val explicitExdates = masterEvent.getProperties<ExDate<*>>(Property.EXDATE)
            .flatMap { exDateProp ->
                val propTzid =
                    exDateProp.getParameter<TzId>("TZID").getOrNull()?.value ?: timeZoneId
                exDateProp.dates.map { temporal -> temporal.toInstant(propTzid) }
            }

        val overrideRecurrenceIds = events
            .mapNotNull { e -> e.getProperty<RecurrenceId<*>>(Property.RECURRENCE_ID).getOrNull() }
            .map { recurrenceIdProp -> recurrenceIdProp.toInstant() }

        val allExdates = (explicitExdates + overrideRecurrenceIds).distinct()

        val overrides = events
            .filter { e -> e.getProperty<RecurrenceId<*>>(Property.RECURRENCE_ID).isPresent }
            .mapNotNull { overrideEvent ->
                val start =
                    overrideEvent.getProperty<DtStart<*>>(Property.DTSTART).getOrNull()?.toInstant()
                        ?: return@mapNotNull null
                val end =
                    overrideEvent.getProperty<DtEnd<*>>(Property.DTEND).getOrNull()?.toInstant()
                        ?: return@mapNotNull null
                val overrideTitle =
                    overrideEvent.getProperty<Summary>(Property.SUMMARY).getOrNull()?.value
                val overrideDesc =
                    overrideEvent.getProperty<Description>(Property.DESCRIPTION).getOrNull()?.value

                OverrideEventDto(
                    start = start.toKotlinInstant(),
                    end = end.toKotlinInstant(),
                    title = overrideTitle,
                    description = overrideDesc
                )
            }

        return CalendarEventEntity(
            href = href,
            uid = uid,
            title = title,
            description = description,
            location = location,
            timeZoneId = timeZoneId,
            firstStart = dtStart,
            firstEnd = dtEnd,
            recurrenceRule = recurrenceRule,
            recurrenceUntil = recurrenceUntil,
            exdates = allExdates,
            overrides = overrides,
            rawIcs = rawIcs,
            eTag = eTag
        )
    }

    private fun DateProperty<*>.toInstant(): Instant {
        val tzid = getParameter<TzId>("TZID").getOrNull()?.value
        return this.date.toInstant(tzid)
    }

    private fun Temporal.toInstant(tzid: String? = null): Instant {
        if (tzid != null) {
            val localDateTime = when (this) {
                is ZonedDateTime -> toLocalDateTime()
                is OffsetDateTime -> toLocalDateTime()
                is LocalDateTime -> this
                else -> LocalDateTime.from(this)
            }
            val zoneId = runCatching { ZoneId.of(tzid) }.getOrDefault(ZoneId.systemDefault())
            return localDateTime.atZone(zoneId).toInstant()
        }

        return when (this) {
            is Instant -> this
            is ZonedDateTime -> toInstant()
            is OffsetDateTime -> toInstant()
            is LocalDateTime -> atZone(ZoneId.systemDefault()).toInstant()
            is LocalDate -> atStartOfDay(ZoneId.systemDefault()).toInstant()
            else -> Instant.from(this)
        }
    }
}