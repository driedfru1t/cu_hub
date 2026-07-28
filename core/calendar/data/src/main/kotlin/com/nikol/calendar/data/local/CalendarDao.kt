package com.nikol.calendar.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CalendarDao {

    @Query(
        """
        SELECT *
        FROM calendarentity
    """
    )
    suspend fun getAll(): List<CalendarEntity>

    @Query(
        """
        SELECT *
        FROM calendarentity
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
        DELETE FROM calendarentity
        WHERE calendarHref = :href
    """
    )
    suspend fun deleteByHref(href: String)

    @Query("DELETE FROM calendarentity")
    suspend fun clear()
}