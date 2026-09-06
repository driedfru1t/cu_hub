package com.nikol.calendar.domain

import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.model.Campus
import com.nikol.calendar.domain.model.CampusBuilding
import com.nikol.calendar.domain.model.EventLocation
import com.nikol.calendar.domain.model.EventType
import com.nikol.calendar.domain.model.ScheduleEvent

object SummaryParser {

    private val ROOM_REGEX = Regex(
        """\b[BFNESW]\d{3,4}(?:-\d+)?\b|Агат|Сетунь|Таганка|Таганке""",
        RegexOption.IGNORE_CASE
    )
    private val FLOOR_REGEX = Regex("""[BFNESW](\d{1,2})\d{2}""", RegexOption.IGNORE_CASE)

    fun parse(
        event: CalendarEvent
    ): ScheduleEvent {
        val rawSummary = event.title.trim()

        val parts = rawSummary
            .split(",")
            .map(String::trim)
            .filter(String::isNotEmpty)

        var endIndex = parts.size
        print(parts)
        var rawRoom = parts.lastOrNull()?.takeIf(::looksLikeLocation)

        rawRoom?.let { --endIndex }

        val rawEventType = parts.getOrNull(endIndex - 1)
            ?.takeIf(EventType::looksLikeEvent)

        rawEventType?.let { --endIndex }

        var title = parts
            .take(endIndex)
            .joinToString(", ")


        val roomInParenMatch = Regex(
            """\(([BFNESW]\d{3,4}(?:\s*[-+]\s*[BFNESW]?\d{3,4})*|Агат|Сетунь)\)""",
            RegexOption.IGNORE_CASE
        ).find(title)
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


        val roomInfo = rawRoom?.let { parseLocation(it) } ?: EventLocation.Unknown
        val eventType = EventType.fromString(rawEventType)

        return ScheduleEvent(
            id = event.id,
            start = event.start,
            end = event.end,
            title = title,
            rawTitle = event.title,
            eventType = eventType,
            customTypeLabel = if (eventType == EventType.UNKNOWN) rawEventType else null,
            eventLocation = roomInfo,
            description = event.description,
            isRetakeWeek = isRetakeWeek,
        )
    }

    private fun looksLikeLocation(text: String): Boolean {
        val upper = text.trim().uppercase()

        return looksLikeRoom(text) ||
                upper.contains("ОНЛАЙН") ||
                upper.contains("ONLINE") ||
                upper.contains("HYBRID") ||
                upper.contains("ТАГАНК") ||
                upper.contains("АГАТ") ||
                upper.contains("СЕТУНЬ")
    }

    private fun looksLikeRoom(text: String): Boolean {
        return ROOM_REGEX.containsMatchIn(text) || text.contains("+")
    }

    private val ROOM_CODE_REGEX =
        Regex("""^[BFNESW]\d{3,4}(?:-\d+)?$""", RegexOption.IGNORE_CASE)

    private fun parseLocation(roomRaw: String): EventLocation {
        val raw = roomRaw.trim()
        val upper = raw.uppercase()

        if (
            upper.contains("ОНЛАЙН") ||
            upper.contains("ONLINE") ||
            upper.contains("HYBRID")
        ) {
            return EventLocation.Online
        }

        if (
            upper.contains("ТАГАНК") ||
            upper.contains("ТАГАНКЕ")
        ) {
            return EventLocation.External(raw)
        }

        if (upper.contains("АГАТ")) {
            return EventLocation.University(
                campus = Campus.DUKAT,
                building = CampusBuilding.DUKAT_FRONTEND,
                floor = 4,
                rooms = listOf("Агат"),
            )
        }

        if (upper.contains("СЕТУНЬ")) {
            return EventLocation.University(
                campus = Campus.DUKAT,
                building = CampusBuilding.DUKAT_FRONTEND,
                floor = 4,
                rooms = listOf("Сетунь"),
            )
        }

        val rooms = raw.splitRooms()

        val firstRoom = rooms.firstOrNull() ?: return EventLocation.Unknown

        val building = when {
            firstRoom.matches(ROOM_CODE_REGEX) -> when (firstRoom.first().uppercaseChar()) {
                'B' -> CampusBuilding.DUKAT_BACKEND
                'F' -> CampusBuilding.DUKAT_FRONTEND
                'W' -> CampusBuilding.CT_WEST
                'N' -> CampusBuilding.CT_NORTH
                'E' -> CampusBuilding.CT_EAST
                'S' -> CampusBuilding.CT_SOUTH
                else -> null
            }

            else -> null
        }

        if (building == null) {
            return EventLocation.Unknown
        }

        return EventLocation.University(
            campus = building.campus,
            building = building,
            floor = extractFloor(firstRoom),
            rooms = rooms
        )
    }

    private fun String.splitRooms(): List<String> {
        return this
            .removeSuffix("(ЦТ)")
            .removeSuffix("(Дукат)")
            .split("+")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun extractFloor(roomCode: String): Int? {
        val match = FLOOR_REGEX.find(roomCode) ?: return null
        return match.groupValues[1].toIntOrNull()
    }
}