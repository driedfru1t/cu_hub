package com.nikol.calendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalendarEntity(
    @PrimaryKey
    val calendarHref: String,
    val syncToken: String,
)
