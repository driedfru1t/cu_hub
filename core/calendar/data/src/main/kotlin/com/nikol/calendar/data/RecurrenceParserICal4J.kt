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


object AndroidTimeZoneRegistry : TimeZoneRegistry {

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

        // computeIfAbsent: вычисляет один раз, КЭШИРУЕТ и больше не вызывает runCatching!
        return zoneIdMap.computeIfAbsent(tzId) { id ->
            runCatching { ZoneId.of(id, TimeZoneRegistry.ZONE_ALIASES) }.getOrNull()
                ?: runCatching { ZoneId.of(id) }.getOrNull()
                ?: ZoneId.systemDefault()
        }
    }

    override fun getTzId(zoneId: String?): String? {
        if (zoneId == null) return null
        return TimeZoneRegistry.ZONE_IDS[zoneId] ?: zoneId
    }
}

private val calendarBuilderThreadLocal = ThreadLocal.withInitial {
    CalendarBuilder(AndroidTimeZoneRegistry)
}

fun getAndroidCalendarBuilder(): CalendarBuilder {
    return calendarBuilderThreadLocal.get()
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
            val searchStart = from.minus(duration).atZone(zoneId)
            val rangeEnd = to.atZone(zoneId)

            val recur = Recur<ZonedDateTime>(entity.recurrenceRule)
            val occurrenceStarts: List<ZonedDateTime> = recur.getDates(seed, searchStart, rangeEnd)

            for (startZdt in occurrenceStarts) {
                val startInstant = startZdt.toInstant()

                if (startInstant in exdateSet) continue

                val endInstant = startInstant.plus(duration)

                resultEvents.add(
                    CalendarEvent(
                        id = entity.href,
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
                        id = entity.href,
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
                        id = entity.href,
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
        val calendar = getAndroidCalendarBuilder().build(rawIcs.reader())
        val events = calendar.getComponents<VEvent>(Component.VEVENT)

        var masterEvent: VEvent? = null
        var minStart: Instant? = null
        var maxEnd: Instant? = null

        val allExdates = mutableSetOf<Instant>()
        val overrides = mutableListOf<OverrideEventDto>()

        for (event in events) {
            val rrule = event.getProperty<RRule<*>>(Property.RRULE)
            val recurrenceId = event.getProperty<RecurrenceId<*>>(Property.RECURRENCE_ID)

            if (masterEvent == null && (rrule.isPresent || recurrenceId.isEmpty)) {
                masterEvent = event
            }
            val startInstant = event.getProperty<DtStart<*>>(Property.DTSTART).getOrNull()?.toInstant()
            if (startInstant != null) {
                if (minStart == null || startInstant < minStart) minStart = startInstant
            }

            val endInstant = event.getProperty<DtEnd<*>>(Property.DTEND).getOrNull()?.toInstant()
            if (endInstant != null) {
                if (maxEnd == null || endInstant > maxEnd) maxEnd = endInstant
            }
            if (recurrenceId.isPresent) {
                val recIdProp = recurrenceId.get()
                allExdates.add(recIdProp.toInstant())

                if (startInstant != null && endInstant != null) {
                    overrides.add(
                        OverrideEventDto(
                            start = startInstant.toKotlinInstant(),
                            end = endInstant.toKotlinInstant(),
                            title = event.getProperty<Summary>(Property.SUMMARY).getOrNull()?.value,
                            description = event.getProperty<Description>(Property.DESCRIPTION).getOrNull()?.value
                        )
                    )
                }
            }
        }

        val finalMaster = masterEvent ?: events.first()
        val uid = finalMaster.uid.orElseThrow().value
        val dtStartProp = finalMaster.getProperty<DtStart<*>>(Property.DTSTART).orElseThrow()
        val dtStart = finalMaster.getProperty<DtStart<*>>(Property.DTSTART).orElseThrow().toInstant()
        val dtEnd = finalMaster.getProperty<DtEnd<*>>(Property.DTEND).orElseThrow().toInstant()

        val timeZoneId = dtStartProp.getParameter<TzId>("TZID").getOrNull()?.value
            ?: calendar.getComponents<net.fortuna.ical4j.model.component.VTimeZone>(Component.VTIMEZONE)
                .firstOrNull()?.timeZoneId?.value

        val explicitExdates = finalMaster.getProperties<ExDate<*>>(Property.EXDATE)
        for (exDateProp in explicitExdates) {
            val propTzid = exDateProp.getParameter<TzId>("TZID").getOrNull()?.value ?: timeZoneId
            for (temporal in exDateProp.dates) {
                allExdates.add(temporal.toInstant(propTzid))
            }
        }

        val rruleProp = finalMaster.getProperty<RRule<*>>(Property.RRULE).getOrNull()
        val recurrenceRule = rruleProp?.value
        val rawUntil = rruleProp?.recur?.until?.toInstant()
        val recurrenceUntil = if (rawUntil != null && dtEnd > rawUntil) dtEnd else rawUntil

        return CalendarEventEntity(
            href = href,
            uid = uid,
            title = finalMaster.getProperty<Summary>(Property.SUMMARY).getOrNull()?.value,
            description = finalMaster.getProperty<Description>(Property.DESCRIPTION).getOrNull()?.value,
            location = finalMaster.getProperty<Location>(Property.LOCATION).getOrNull()?.value,
            timeZoneId = timeZoneId,
            firstStart = dtStart,
            firstEnd = dtEnd,
            recurrenceRule = recurrenceRule,
            recurrenceUntil = recurrenceUntil,
            exdates = allExdates.toList(),
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