package com.nikol.calendar.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface CalendarEventDao {

    @Query(
        """
        SELECT *
        FROM calendar_events
        ORDER BY firstStart ASC
        """
    )
    fun observeEvents(): Flow<List<CalendarEventEntity>>


    @Query(
        """
        SELECT *
        FROM calendar_events
        WHERE 
            (
                recurrenceRule IS NULL
                AND firstStart < :end
                AND firstEnd > :start
            )
            OR
            (
                recurrenceRule IS NOT NULL
                AND firstStart < :end
                AND (
                    recurrenceUntil IS NULL
                    OR recurrenceUntil > :start
                )
            )
        ORDER BY firstStart ASC
        """
    )
    fun observeEvents(
        start: Instant,
        end: Instant
    ): Flow<List<CalendarEventEntity>>


    @Query(
        """
        SELECT *
        FROM calendar_events
        WHERE href = :href
        """
    )
    suspend fun getByHref(
        href: String
    ): CalendarEventEntity?


    @Upsert
    suspend fun upsert(
        event: CalendarEventEntity
    )


    @Upsert
    suspend fun upsert(
        events: List<CalendarEventEntity>
    )


    @Query(
        """
        DELETE FROM calendar_events
        WHERE href = :href
        """
    )
    suspend fun deleteByHref(
        href: String
    )


    @Query(
        """
        DELETE FROM calendar_events
        WHERE href IN (:hrefs)
        """
    )
    suspend fun deleteByHrefs(
        hrefs: List<String>
    )


    @Query(
        """
        DELETE FROM calendar_events
        """
    )
    suspend fun clear()
}
