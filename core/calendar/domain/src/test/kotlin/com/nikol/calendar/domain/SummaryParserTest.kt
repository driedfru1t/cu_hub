package com.nikol.calendar.domain

import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.model.EventType
import com.nikol.calendar.domain.model.Tower
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SummaryParserTest {

    private val dummyStart: Instant = Instant.parse("2025-09-08T08:30:00Z")
    private val dummyEnd: Instant = Instant.parse("2025-09-08T09:50:00Z")

    private fun createCalendarEvent(
        id: String = "test-id-123",
        title: String,
        description: String? = null,
        start: Instant = dummyStart,
        end: Instant = dummyEnd
    ): CalendarEvent {
        return CalendarEvent(
            id = id,
            start = start,
            end = end,
            title = title,
            description = description
        )
    }

    @Test
    fun `parse classic 3-part event with emojis remaining in title`() {
        val calendarEvent = createCalendarEvent(
            id = "test-1",
            title = "🔵 🔵 Основы математического анализа и линейной алгебры, Семинар, B210",
            description = "https://centraluniversity.ktalk.ru/123"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertEquals("test-1", result.id)
        assertEquals("🔵 🔵 Основы математического анализа и линейной алгебры", result.title)
        assertEquals("🔵 🔵 Основы математического анализа и линейной алгебры, Семинар, B210", result.rawTitle)
        assertEquals(EventType.SEMINAR, result.eventType)
        assertNull(result.customTypeLabel)
        assertTrue(result.badges.isEmpty())
        assertEquals("https://centraluniversity.ktalk.ru/123", result.description)
        assertNull(result.teacherName)
        assertFalse(result.isRetakeWeek)
        assertFalse(result.isOfficeHours)

        // checkNotNull гарантирует смарт-каст RoomInfo? в non-null RoomInfo
        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.BACKEND, room.tower)
        assertEquals(2, room.floor)
        assertEquals(listOf("B210"), room.rooms)
        assertFalse(room.isCombined)
        assertFalse(room.isNamedRoom)
        assertFalse(room.isOnline)
    }

    @Test
    fun `parse event with combined rooms`() {
        val calendarEvent = createCalendarEvent(
            title = "🔵 🔵 Основы математического анализа и линейной алгебры, Контрольная, B202 + B204 + B206"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertEquals(EventType.TEST, result.eventType)

        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.BACKEND, room.tower)
        assertEquals(2, room.floor)
        assertEquals(listOf("B202", "B204", "B206"), room.rooms)
        assertTrue(room.isCombined)
    }

    @Test
    fun `parse event with named room Agat on 4th floor of Tower Frontend`() {
        val calendarEvent = createCalendarEvent(
            title = "🔴 Введение в статистику. Основной уровень, Контрольная, Агат"
        )

        val result = SummaryParser.parse(calendarEvent)

        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.FRONTEND, room.tower)
        assertEquals(4, room.floor, "Именованные аудитории должны быть на 4 этаже")
        assertTrue(room.isNamedRoom)
        assertEquals(listOf("Агат"), room.rooms)
    }

    @Test
    fun `parse event with named room Setun`() {
        val calendarEvent = createCalendarEvent(
            title = "Основы российской государственности_3, Лекция, Сетунь"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertEquals(EventType.LECTURE, result.eventType)

        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.FRONTEND, room.tower)
        assertEquals(4, room.floor)
        assertTrue(room.isNamedRoom)
    }

    @Test
    fun `parse event with teacher name and room range in parentheses`() {
        val calendarEvent = createCalendarEvent(
            title = "Большие идеи в компьютерных науках, Булат Ибрагимов (B712-B714)"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertEquals("Большие идеи в компьютерных науках", result.title)
        assertEquals("Булат Ибрагимов", result.teacherName)

        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.BACKEND, room.tower)
        assertEquals(7, room.floor)
        assertEquals(listOf("B712", "B714"), room.rooms)
        assertTrue(room.isCombined)
    }

    @Test
    fun `parse event with retake week flag`() {
        val calendarEvent = createCalendarEvent(
            title = "🔵 🔵 Основы математического анализа и линейной алгебры. Коллоквиум. Неделя дорешек, Коллоквиум, B202 + B204 + B206"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertTrue(result.isRetakeWeek)
        assertEquals(EventType.COLLOQUIUM, result.eventType)
    }

    @Test
    fun `parse office hours event`() {
        val calendarEvent = createCalendarEvent(
            title = "🔴 ⚫️  Основы бизнес-аналитики, Office hours, B206"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertTrue(result.isOfficeHours)
        assertEquals(EventType.OFFICE_HOURS, result.eventType)

        val room = checkNotNull(result.roomInfo)
        assertEquals(2, room.floor)
    }

    @Test
    fun `parse simple event without room or type`() {
        val calendarEvent = createCalendarEvent(
            title = "CUPutt"
        )

        val result = SummaryParser.parse(calendarEvent)

        assertEquals("CUPutt", result.title)
        assertEquals(EventType.UNKNOWN, result.eventType)
        assertNull(result.roomInfo)
    }

    @Test
    fun `parse external PE event on Taganka`() {
        val calendarEvent = createCalendarEvent(
            title = "Физкультура на Таганке (по записи)"
        )

        val result = SummaryParser.parse(calendarEvent)

        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.EXTERNAL, room.tower)
        assertNull(room.floor)
    }

    @Test
    fun `parse event on 10th floor of Tower Backend`() {
        val calendarEvent = createCalendarEvent(
            title = "Основы разработки на Go, Зачет, B1002"
        )

        val result = SummaryParser.parse(calendarEvent)

        val room = checkNotNull(result.roomInfo)
        assertEquals(Tower.BACKEND, room.tower)
        assertEquals(10, room.floor)
    }
}