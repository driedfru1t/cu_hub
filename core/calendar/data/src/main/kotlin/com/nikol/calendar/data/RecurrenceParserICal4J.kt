package com.nikol.calendar.data

import com.nikol.calendar.data.local.CalendarEventEntity
import com.nikol.calendar.domain.model.CalendarEvent
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.Recur
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
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

class RecurrenceParserICal4J @Inject constructor() : RecurrenceParser {
    override fun expand(
        entity: CalendarEventEntity,
        from: Instant,
        to: Instant
    ): List<CalendarEvent> {
        val zoneId = entity.timeZoneId?.let { runCatching { ZoneId.of(it) }.getOrNull() }
            ?: ZoneId.systemDefault()

        val resultEvents = mutableListOf<CalendarEvent>()

        if (entity.recurrenceRule != null) {
            val duration = Duration.between(entity.firstStart, entity.firstEnd)
            val seed = entity.firstStart.atZone(zoneId)
            val rangeStart = from.atZone(zoneId)
            val rangeEnd = to.atZone(zoneId)

            val recur = Recur<ZonedDateTime>(entity.recurrenceRule)
            val occurrenceStarts: List<ZonedDateTime> = recur.getDates(seed, rangeStart, rangeEnd)

            val exdateSet: Set<Instant> = entity.exdates.toSet()

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
            if (entity.firstStart < to && entity.firstEnd > from) {
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

        if (entity.rawIcs.isNotBlank()) {
            val calendar = CalendarBuilder().build(entity.rawIcs.reader())
            val allVEvents = calendar.getComponents<VEvent>(Component.VEVENT)
            val overrideEvents = allVEvents.filter {
                it.getProperty<RecurrenceId<*>>(Property.RECURRENCE_ID).isPresent
            }

            for (overrideEvent in overrideEvents) {
                val dtStart =
                    overrideEvent.getProperty<DtStart<*>>(Property.DTSTART).getOrNull()?.toInstant()
                        ?: continue
                val dtEnd =
                    overrideEvent.getProperty<DtEnd<*>>(Property.DTEND).getOrNull()?.toInstant()
                        ?: continue

                if (dtStart < to && dtEnd > from) {
                    val title =
                        overrideEvent.getProperty<Summary>(Property.SUMMARY).getOrNull()?.value
                            ?: entity.title
                            ?: ""

                    val description =
                        overrideEvent.getProperty<Description>(Property.DESCRIPTION)
                            .getOrNull()?.value
                            ?: entity.description

                    resultEvents.add(
                        CalendarEvent(
                            id = entity.uid,
                            title = title,
                            description = description,
                            start = dtStart,
                            end = dtEnd
                        )
                    )
                }
            }
        }

        return resultEvents
    }

    override fun parse(
        href: String,
        eTag: String,
        rawIcs: String
    ): CalendarEventEntity {
        val calendar = CalendarBuilder().build(rawIcs.reader())
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

        val dtStartProp = masterEvent.getProperty<DtStart<*>>(Property.DTSTART).orElseThrow()
        val dtEndProp = masterEvent.getProperty<DtEnd<*>>(Property.DTEND).orElseThrow()

        val timeZoneId = dtStartProp.getParameter<TzId>("TZID").getOrNull()?.value

        val dtStart = dtStartProp.toInstant()
        val dtEnd = dtEndProp.toInstant()

        val rruleProp = masterEvent.getProperty<RRule<*>>(Property.RRULE).getOrNull()
        val recurrenceRule = rruleProp?.value
        val recurrenceUntil = rruleProp?.recur?.until?.toInstant()

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