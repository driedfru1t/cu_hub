package com.nikol.calendar.domain

import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.model.Campus
import com.nikol.calendar.domain.model.CampusBuilding
import com.nikol.calendar.domain.model.EventLocation
import com.nikol.calendar.domain.model.EventType
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class SummaryParserTest {
    @Test
    fun `parse online curator event`() {
        val event = CalendarEvent(
            id = "1",
            start = Instant.parse("2026-09-02T10:00:00Z"),
            end = Instant.parse("2026-09-02T11:00:00Z"),
            title = "\uD83D\uDD34 Теория вероятностей. Основной уровень, Семинар, S306-2 + S306-1 (ЦТ)",
            description = null
        )

        val result = SummaryParser.parse(event)

        assertEquals(
            "\uD83D\uDD34 Теория вероятностей. Основной уровень",
            result.title
        )

        assertEquals(
            EventType.SEMINAR,
            result.eventType
        )

        assertEquals(
            EventLocation.University(
                campus = Campus.CENTRAL_TELEGRAPH,
                building = CampusBuilding.CT_SOUTH,
                floor = 3,
                rooms = listOf("S306-2", "S306-1"),
            ),
            result.eventLocation
        )

        assertNull(result.customTypeLabel)
        assertFalse(result.isRetakeWeek)
    }
}