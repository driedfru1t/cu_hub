package com.nikol.calendar.data

import com.nikol.calendar.data.local.CalendarEventEntity
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RecurrenceParserICal4JTest {

    private lateinit var parser: RecurrenceParserICal4J
    private lateinit var recurringIcs: String

    @Before
    fun setUp() {
        parser = RecurrenceParserICal4J()

        recurringIcs = javaClass
            .getResource("/ics/recurring.ics")!!
            .readText()
    }

    @Test
    fun `parse yandex recurring event - basic metadata and timestamps`() {
        val href = "08ddea54-d1df-2dd3-6ff2-450001069bee.ics"
        val eTag = "1772547885845"

        val result = parser.parse(
            href = href,
            eTag = eTag,
            rawIcs = recurringIcs
        )

        // Идентификаторы и служебные поля
        assertEquals(href, result.href)
        assertEquals(eTag, result.eTag)
        assertEquals("08ddea54-d1df-2dd3-6ff2-450001069bee", result.uid)
        assertEquals(recurringIcs, result.rawIcs)

        // Метаданные события (Summary, Description, Location, TimeZone)
        assertEquals(
            "🔵 🔵 Основы математического анализа и линейной алгебры, Семинар, B210",
            result.title
        )
        assertEquals(
            "https://centraluniversity.ktalk.ru/08ddea54d1df2dd36ff2450001069bee",
            result.description
        )
        assertNull(result.location) // LOCATION в исходном VEVENT отсутствует
        assertEquals("Europe/Moscow", result.timeZoneId)

        // Даты начала и окончания (DTSTART: 11:30 MSK -> 08:30 UTC)
        assertEquals(Instant.parse("2025-09-08T08:30:00Z"), result.firstStart)
        assertEquals(Instant.parse("2025-09-08T09:50:00Z"), result.firstEnd)

        // Правило повторения (RRULE) и граница (UNTIL)
        assertEquals(
            "FREQ=WEEKLY;BYDAY=MO;UNTIL=20251226T093000Z;INTERVAL=1".split(';').toSet(),
            result.recurrenceRule?.split(';')?.toSet()
        )
        assertEquals(Instant.parse("2025-12-26T09:30:00Z"), result.recurrenceUntil)
    }

    @Test
    fun `parse yandex recurring event - exdates includes EXDATE and RECURRENCE-ID`() {
        val result = parser.parse(
            href = "08ddea54-d1df-2dd3-6ff2-450001069bee.ics",
            eTag = "1772547885845",
            rawIcs = recurringIcs
        )

        // 1. EXDATE;TZID=Europe/Moscow:20251222T113000  -> 2025-12-22T08:30:00Z (Отменённый семинар 22 декабря)
        // 2. RECURRENCE-ID;TZID=Europe/Moscow:20251215T113000 -> 2025-12-15T08:30:00Z (Переопределённый семинар 15 декабря)
        val expectedExdates = setOf(
            Instant.parse("2025-12-22T08:30:00Z"),
            Instant.parse("2025-12-15T08:30:00Z")
        )

        assertEquals(2, result.exdates.size)
        assertEquals(expectedExdates, result.exdates.toSet())
    }

    @Test
    fun `expand yandex recurring event for full semester`() {
        val entity = parser.parse(
            href = "08ddea54-d1df-2dd3-6ff2-450001069bee.ics",
            eTag = "1772547885845",
            rawIcs = recurringIcs
        )

        // Диапазон всего осеннего семестра (с 1 сентября по 31 декабря 2025)
        val from = Instant.parse("2025-09-01T00:00:00Z")
        val to = Instant.parse("2025-12-31T23:59:59Z")

        val expandedEvents = parser.expand(entity, from, to)

        // Всего 16 понедельников в семестре минус 1 отменённый (22 декабря) = 15 занятий
        assertEquals(15, expandedEvents.size)

        // 1. Проверяем, что занятия 22 декабря НЕТ в списке (отменено)
        val hasEventOnDec22 = expandedEvents.any { event ->
            event.start == Instant.parse("2025-12-22T08:30:00Z")
        }
        assertFalse(hasEventOnDec22, "Занятие на 22 декабря должно быть отменено!")

        // 2. Проверяем, что переопределённое занятие 15 декабря ПРИСУТСТВУЕТ
        val dec15Event = expandedEvents.find { event ->
            event.start == Instant.parse("2025-12-15T08:30:00Z")
        }
        assertTrue(dec15Event != null, "Переопределённое занятие на 15 декабря должно быть в расписании!")
        assertEquals(Instant.parse("2025-12-15T09:50:00Z"), dec15Event.end)

        // 3. Проверяем, что итоговый список отсортирован по хронологии
        val isSorted = expandedEvents.zipWithNext().all { (a, b) -> a.start <= b.start }
        assertTrue(isSorted, "Все события должны быть строго отсортированы по времени начала")
    }

    @Test
    fun `expand yandex recurring event for range containing canceled date`() {
        val entity = parser.parse(
            href = "08ddea54-d1df-2dd3-6ff2-450001069bee.ics",
            eTag = "1772547885845",
            rawIcs = recurringIcs
        )

        // Диапазон включает только отменённый понедельник 22 декабря 2025 года
        val from = Instant.parse("2025-12-20T00:00:00Z")
        val to = Instant.parse("2025-12-25T23:59:59Z")

        val expandedEvents = parser.expand(entity, from, to)

        // В этом диапазоне быть ничего не должно
        assertTrue(expandedEvents.isEmpty(), "В диапазоне отменённого занятия список должен быть пустым")
    }

    @Test
    fun `expand yandex recurring event for single week`() {
        val entity = parser.parse(
            href = "08ddea54-d1df-2dd3-6ff2-450001069bee.ics",
            eTag = "1772547885845",
            rawIcs = recurringIcs
        )

        // Диапазон первой учебной недели (с 8 по 14 сентября 2025 года)
        val from = Instant.parse("2025-09-08T00:00:00Z")
        val to = Instant.parse("2025-09-14T23:59:59Z")

        val expandedEvents = parser.expand(entity, from, to)

        assertEquals(1, expandedEvents.size)
        val event = expandedEvents.first()
        assertEquals(Instant.parse("2025-09-08T08:30:00Z"), event.start)
        assertEquals(Instant.parse("2025-09-08T09:50:00Z"), event.end)
        assertEquals(
            "🔵 🔵 Основы математического анализа и линейной алгебры, Семинар, B210",
            event.title
        )
    }

    @Test
    fun `expand single non-recurring event`() {
        val singleEntity = CalendarEventEntity(
            href = "single-event.ics",
            uid = "single-uid-123",
            title = "Консультация к экзамену",
            description = "Аудитория B101",
            location = "B101",
            timeZoneId = "Europe/Moscow",
            firstStart = Instant.parse("2025-10-01T08:30:00Z"),
            firstEnd = Instant.parse("2025-10-01T09:50:00Z"),
            recurrenceRule = null,
            recurrenceUntil = null,
            exdates = emptyList(),
            rawIcs = "",
            eTag = "12345"
        )

        // Запрос интервала, содержащего событие
        val eventsInWindow = parser.expand(
            entity = singleEntity,
            from = Instant.parse("2025-10-01T00:00:00Z"),
            to = Instant.parse("2025-10-01T23:59:59Z")
        )

        assertEquals(1, eventsInWindow.size)
        assertEquals("Консультация к экзамену", eventsInWindow.first().title)

        // Запрос интервала вне события
        val eventsOutsideWindow = parser.expand(
            entity = singleEntity,
            from = Instant.parse("2025-10-02T00:00:00Z"),
            to = Instant.parse("2025-10-02T23:59:59Z")
        )

        assertTrue(eventsOutsideWindow.isEmpty())
    }
}