package com.nikol.calendar.domain

import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.model.EventBadgeColor
import com.nikol.calendar.domain.model.EventType
import com.nikol.calendar.domain.model.RoomInfo
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.calendar.domain.model.Tower

object SummaryParser {

    // Добавлен флаг (?U) перед \b — заставляет \b понимать кириллицу (Таганка, Агат...)
    private val ROOM_REGEX = Regex(
        """(?U)\b([BF]\d{3,4}(?:\s*[\+-]\s*[BF]?\d{3,4})*|[BF]\d{3,4}-[BF]?\d{3,4}|Агат|Сетунь|Таганка|Таганке)\b""",
        RegexOption.IGNORE_CASE
    )

    private val NAMED_ROOMS = setOf("Агат", "Сетунь")
    private val FLOOR_REGEX = Regex("""[BF](\d{1,2})\d{2}""", RegexOption.IGNORE_CASE)

    fun parse(
        event: CalendarEvent
    ): ScheduleEvent {
        val badges = mutableListOf<EventBadgeColor>()
        val rawSummary = event.title.trim()

        var title = rawSummary
        var rawEventType: String? = null
        var rawRoom: String? = null
        var teacherName: String? = null

        val teacherMatch = Regex("""^(.*?),\s*([А-Яа-яA-Za-z\s]+)\s*\(([BF]\d{3,4}.*?)\)$""").find(rawSummary)
        if (teacherMatch != null) {
            title = teacherMatch.groupValues[1].trim()
            teacherName = teacherMatch.groupValues[2].trim()
            rawRoom = teacherMatch.groupValues[3].trim()
        } else {
            val parts = rawSummary.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            when (parts.size) {
                3 -> {
                    title = parts[0]
                    rawEventType = parts[1]
                    rawRoom = parts[2]
                }
                2 -> {
                    title = parts[0]
                    if (looksLikeRoom(parts[1])) {
                        rawRoom = parts[1]
                    } else {
                        rawEventType = parts[1]
                    }
                }
                1 -> {
                    title = parts[0]
                }
                else -> {
                    title = parts[0]
                    rawEventType = parts[1]
                    rawRoom = parts.subList(2, parts.size).joinToString(", ")
                }
            }
        }

        val roomInParenMatch = Regex("""\((B\d{3,4}.*?|F\d{3,4}.*?)\)""").find(title)
        if (roomInParenMatch != null) {
            if (rawRoom == null) {
                rawRoom = roomInParenMatch.groupValues[1]
            }
            title = title.replace(roomInParenMatch.value, "").trim()
        }

        if (rawRoom == null) {
            rawRoom = ROOM_REGEX.find(rawSummary)?.value
        }

        val isRetakeWeek = rawSummary.contains("Неделя дорешек", ignoreCase = true)
        val isOfficeHours = rawSummary.contains("Office hours", ignoreCase = true) || rawSummary.contains("OH ", ignoreCase = true)

        val roomInfo = rawRoom?.let { parseRoomInfo(it) }
        val eventType = EventType.fromString(rawEventType)

        return ScheduleEvent(
            id = event.id,
            start = event.start,
            end = event.end,
            title = title,
            rawTitle = event.title,
            eventType = eventType,
            customTypeLabel = if (eventType == EventType.UNKNOWN || eventType == EventType.OTHER) rawEventType else null,
            badges = badges,
            roomInfo = roomInfo,
            teacherName = teacherName,
            description = event.description,
            isRetakeWeek = isRetakeWeek,
            isOfficeHours = isOfficeHours
        )
    }

    private fun looksLikeRoom(text: String): Boolean {
        return ROOM_REGEX.containsMatchIn(text) || text.contains("+")
    }

    private fun parseRoomInfo(roomRaw: String): RoomInfo {
        val tower = Tower.fromRoomString(roomRaw)
        val isNamed = NAMED_ROOMS.any { roomRaw.contains(it, ignoreCase = true) }

        val rooms = when {
            roomRaw.contains("+") -> roomRaw.split("+").map { it.trim() }
            roomRaw.contains("-") && !roomRaw.contains("Таганке", ignoreCase = true) -> {
                roomRaw.split("-").map { it.trim() }
            }
            else -> listOf(roomRaw.trim())
        }

        val floor = if (isNamed) {
            4
        } else {
            extractFloor(rooms.firstOrNull() ?: roomRaw)
        }

        return RoomInfo(
            raw = roomRaw,
            rooms = rooms,
            tower = tower,
            floor = floor,
            isNamedRoom = isNamed
        )
    }

    private fun extractFloor(roomCode: String): Int? {
        val match = FLOOR_REGEX.find(roomCode) ?: return null
        return match.groupValues[1].toIntOrNull()
    }
}