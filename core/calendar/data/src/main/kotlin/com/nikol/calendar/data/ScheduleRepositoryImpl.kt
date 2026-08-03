package com.nikol.calendar.data

import android.util.Log
import arrow.core.raise.context.Raise
import arrow.core.raise.context.withError
import arrow.fx.coroutines.parMap
import com.nikol.calendar.data.local.CalendarDao
import com.nikol.calendar.data.local.CalendarEntity
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.calendar.data.local.CalendarEventEntity
import com.nikol.calendar.data.mapper.toScheduleError
import com.nikol.calendar.data.remote.CalDavError
import com.nikol.calendar.data.remote.CalDavService
import com.nikol.calendar.data.remote.CalendarSyncDTO
import com.nikol.calendar.data.remote.ResponseCalendarDTO
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.repo.ScheduleRepository
import com.nikol.sync.SyncResult
import com.nikol.sync.Syncable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

internal data class DatabaseChanges(
    val delete: List<String>,
    val upsert: List<CalendarEventEntity>
)

class ScheduleRepositoryImpl @Inject constructor(
    private val calDavService: CalDavService,
    private val calendarEventDao: CalendarEventDao,
    private val calendarDao: CalendarDao,
    private val recurrenceParser: RecurrenceParser
) : ScheduleRepository, Syncable {

    override fun observeEvents(
        start: Instant,
        end: Instant
    ): Flow<List<CalendarEvent>> {
        return calendarEventDao.observeEvents(start, end).map { entities ->
            val expandedEvents = entities.flatMap { entity ->
                val expanded = recurrenceParser.expand(
                    entity = entity,
                    from = start,
                    to = end
                )
                expanded
            }
            val distinctEvents = expandedEvents.distinctBy { event -> event.id to event.start }
            val sorted = distinctEvents.sortedBy { e -> e.start }
            sorted
        }
    }

    override suspend fun getEvent(href: String): CalendarEvent? {
        TODO("Not yet implemented")
    }

    context(raise: Raise<CalDavError>)
    private suspend fun discoverCalendarHrefs(): List<String> {
        val principal = with(calDavService) { raise.discoverPrincipals() }
        val home = with(calDavService) { raise.discoverCalendarPath(principal) }
        return with(calDavService) { raise.getCalendarsPath(home) }
    }

    private fun ResponseCalendarDTO.toDatabaseChanges(): DatabaseChanges {
        val toDelete = mutableListOf<String>()
        val toUpsert = mutableListOf<CalendarEventEntity>()

        events.forEach { event ->
            when (event) {
                is CalendarSyncDTO.Delete -> toDelete += event.href

                is CalendarSyncDTO.Upsert -> {
                    if (isUniversityEvent(event.calendarData)) {
                        runCatching {
                            recurrenceParser.parse(
                                event.href,
                                event.eTag,
                                event.calendarData
                            )
                        }.onSuccess { entity ->
                            toUpsert += entity
                        }.onFailure { error ->
                            Log.e("Sync", "Failed to parse ICS for href: ${event.href}", error)
                        }
                    } else {
                        toDelete += event.href
                    }
                }
            }
        }
        return DatabaseChanges(toDelete, toUpsert)
    }

    private val UNIVERSITY_ORGANIZER_REGEX = Regex(
        pattern = """^ORGANIZER(?:;[^:\r\n]*)?:.*?(?:timetable@centraluniversity\.ru|ЦУ\s+Расписание)""",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    fun isUniversityEvent(rawIcs: String): Boolean {
        return UNIVERSITY_ORGANIZER_REGEX.containsMatchIn(rawIcs)
    }

    private suspend fun applyResponse(
        calendarHref: String,
        response: ResponseCalendarDTO
    ) {
        val changes = response.toDatabaseChanges()
        calendarEventDao.deleteByHrefs(changes.delete)
        calendarEventDao.upsert(changes.upsert)

        calendarDao.upsert(
            CalendarEntity(
                calendarHref,
                response.syncToken
            )
        )
    }

    context(raise: Raise<CalDavError>)
    private suspend fun syncCalendars(
        hrefs: List<String>
    ) {

        val response = hrefs.parMap(concurrency = 4) { calendarHref ->
            val syncToken = calendarDao.getByHref(calendarHref)?.syncToken
            val discoverResult = with(calDavService) {
                raise.discoverCalendars(
                    path = calendarHref,
                    syncToken = syncToken
                )
            }
            calendarHref to discoverResult
        }

        response.forEach { (calendarHref, discoverResult) ->
            applyResponse(calendarHref, discoverResult)
        }
    }

    context(raise: Raise<ScheduleError>)
    override suspend fun refresh() {
        withError(CalDavError::toScheduleError) {
            val calendarHrefs = discoverCalendarHrefs()
            Log.d("DEBUG", calendarHrefs.toString())
            syncCalendars(calendarHrefs)
        }
    }

    override suspend fun sync(): SyncResult {
        TODO("Not yet implemented")
    }
}