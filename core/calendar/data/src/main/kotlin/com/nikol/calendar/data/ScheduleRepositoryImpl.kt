package com.nikol.calendar.data

import android.util.Log
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import arrow.core.raise.context.withError
import arrow.core.raise.recover
import arrow.fx.coroutines.parMap
import com.nikol.calendar.data.local.CalendarDao
import com.nikol.calendar.data.local.CalendarEntity
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.calendar.data.local.CalendarEventEntity
import com.nikol.calendar.data.mapper.toScheduleError
import com.nikol.calendar.data.remote.CalDavError
import com.nikol.calendar.data.remote.CalDavService
import com.nikol.calendar.data.remote.CalendarSyncDTO
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.repo.ScheduleRepository
import com.nikol.sync.SyncResult
import com.nikol.sync.Syncable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.time.toJavaInstant

data class DatabaseChanges(
    val delete: List<String>,
    val upsert: List<CalendarEventEntity>
)

class ScheduleRepositoryImpl @Inject constructor(
    private val calDavService: CalDavService,
    private val calendarEventDao: CalendarEventDao,
    private val calendarDao: CalendarDao,
    private val recurrenceParser: RecurrenceParser,
) : ScheduleRepository, Syncable {

    override fun observeEvents(
        start: Instant,
        end: Instant
    ): Flow<List<CalendarEvent>> {
        return calendarEventDao.observeEvents(start, end).map { entities ->
            val expandedEvents = entities.flatMap { entity ->
                recurrenceParser.expand(
                    entity = entity,
                    from = start,
                    to = end
                )
            }
            val distinctEvents = expandedEvents.distinctBy { event -> event.id to event.start }
            val sorted = distinctEvents.sortedBy { e -> e.start }
            sorted
        }
    }

    context(raise: Raise<ScheduleError>)
    override suspend fun getEvent(href: String, start: Instant): CalendarEvent {
        val event = calendarEventDao.getByHref(href) ?: raise.raise(ScheduleError.NotFound)
        val override = event.overrides.find {
            it.start.toJavaInstant() == start
        }

        return if (override != null) {
            CalendarEvent(
                id = href,
                start = override.start.toJavaInstant(),
                end = override.end.toJavaInstant(),
                title = override.title ?: "",
                description = override.description
            )
        } else {
            CalendarEvent(
                id = href,
                start = start,
                end = start.plus(Duration.between(event.firstStart, event.firstEnd)),
                title = event.title ?: "",
                description = event.description
            )
        }
    }

    context(raise: Raise<CalDavError>)
    private suspend fun discoverCalendarHrefs(): List<String> {
        val principal = with(calDavService) { raise.discoverPrincipals() }
        val home = with(calDavService) { raise.discoverCalendarPath(principal) }
        return with(calDavService) { raise.getCalendarsPath(home) }
    }


    private val UNIVERSITY_ORGANIZER_REGEX = Regex(
        pattern = """^ORGANIZER(?:;[^:\r\n]*)?:.*?(?:timetable@centraluniversity\.ru|ЦУ\s+Расписание)""",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    fun isUniversityEvent(rawIcs: String): Boolean {
        return UNIVERSITY_ORGANIZER_REGEX.containsMatchIn(rawIcs)
    }

    fun Iterable<CalendarSyncDTO>.toDatabaseChanges(): DatabaseChanges {
        val upsert = mutableListOf<CalendarEventEntity>()
        val delete = mutableListOf<String>()
        for (event in this@toDatabaseChanges) {
            when (event) {
                is CalendarSyncDTO.Delete -> delete += event.href
                is CalendarSyncDTO.Upsert -> {
                    if (isUniversityEvent(event.calendarData)) {
                        runCatching {
                            recurrenceParser.parse(
                                event.href,
                                event.eTag,
                                event.calendarData
                            )
                        }.onSuccess { entity ->
                            upsert += entity
                        }.onFailure { error ->
                            Log.e(
                                "Sync",
                                "Failed to parse ICS for href: ${event.href}",
                                error
                            )
                        }
                    } else {
                        delete += event.href
                    }
                }
            }
        }
        return DatabaseChanges(delete, upsert)
    }

    context(raise: Raise<CalDavError>)
    private suspend fun syncCalendars(
        hrefs: List<String>
    ) {
        hrefs.parMap(concurrency = 2) { calendarHref ->
            val syncToken = calendarDao.getByHref(calendarHref)?.syncToken
            recover({
                executeSync(calendarHref, syncToken)
            }) {
                if (it is CalDavError.SyncTokenExpired) {
                    calendarDao.deleteByHref(calendarHref)
                    executeSync(calendarHref, null)
                } else {
                    raise(it)
                }
            }
        }
    }

    context(raise: Raise<CalDavError>)
    suspend fun executeSync(calendarHref: String, token: String?) {
        calDavService.syncCalendars(
            path = calendarHref,
            syncToken = token,
            saveBatch = { calendarEventDao.sync(it.toDatabaseChanges()) },
            saveToken = { calendarDao.upsert(CalendarEntity(calendarHref, it)) }
        )
    }

    context(raise: Raise<ScheduleError>)
    override suspend fun refresh() {
        withError(CalDavError::toScheduleError) {
            val calendarHrefs = discoverCalendarHrefs()
            syncCalendars(calendarHrefs)
        }
    }

    override suspend fun sync(): SyncResult {
        TODO("Not yet implemented")
    }
}