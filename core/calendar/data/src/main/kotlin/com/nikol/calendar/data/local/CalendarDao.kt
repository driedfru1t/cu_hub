package com.nikol.calendar.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CalendarDao {

    @Query(
        """
        SELECT *
        FROM calendar_entity
    """
    )
    suspend fun getAll(): List<CalendarEntity>

    @Query(
        """
        SELECT *
        FROM calendar_entity
        WHERE calendarHref = :href
    """
    )
    suspend fun getByHref(href: String): CalendarEntity?

    @Upsert
    suspend fun upsert(calendar: CalendarEntity)

    @Upsert
    suspend fun upsert(calendars: List<CalendarEntity>)

    @Query(
        """
        DELETE FROM calendar_entity
        WHERE calendarHref = :href
    """
    )
    suspend fun deleteByHref(href: String)

    @Query("DELETE FROM calendar_entity")
    suspend fun clear()
}