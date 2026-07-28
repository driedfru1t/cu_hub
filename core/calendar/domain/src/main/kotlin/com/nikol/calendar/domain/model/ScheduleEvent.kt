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
    val badges: List<EventBadgeColor>,
    val roomInfo: RoomInfo?,
    val teacherName: String? = null,
    val description: String? = null,
    val isRetakeWeek: Boolean = false,
    val isOfficeHours: Boolean = false
)
