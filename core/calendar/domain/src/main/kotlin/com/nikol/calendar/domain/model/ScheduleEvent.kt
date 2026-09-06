package com.nikol.calendar.domain.model

import java.time.Instant

data class ScheduleEvent(
    val id: String,
    val start: Instant,
    val end: Instant,
    val title: String,
    val rawTitle: String,
    val eventType: EventType,
    val customTypeLabel: String?,
    val eventLocation: EventLocation,
    val description: String? = null,
    val isRetakeWeek: Boolean = false,
)
