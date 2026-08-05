package com.nikol.calendar.domain.model

import java.time.Instant

data class CalendarEvent(
    val id: String,
    val start: Instant,
    val end: Instant,
    val title: String,
    val description: String?,
)