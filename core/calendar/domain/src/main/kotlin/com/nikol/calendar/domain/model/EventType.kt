package com.nikol.calendar.domain.model

enum class EventType(val displayName: String) {
    LECTURE("Лекция"),
    SEMINAR("Семинар"),
    TEST("Контрольная"),
    COLLOQUIUM("Коллоквиум"),
    EXAM("Экзамен"),
    CREDIT("Зачет"),
    OFFICE_HOURS("Office Hours"),
    INTERNAL_EVENT("Внутреннее мероприятие"),
    OTHER("Другое"),
    UNKNOWN("Событие"); // Для произвольных названий без формата ("CUPutt", "Посвящение в студенты")

    companion object {
        fun fromString(raw: String?): EventType {
            if (raw == null) return UNKNOWN
            val clean = raw.trim().lowercase()
            return when {
                clean.contains("лекци") -> LECTURE
                clean.contains("семинар") -> SEMINAR
                clean.contains("контрольн") || clean.contains("контест") -> TEST
                clean.contains("коллоквиум") -> COLLOQUIUM
                clean.contains("экзамен") -> EXAM
                clean.contains("зачет") || clean.contains("зачёт") -> CREDIT
                clean.contains("office hours") || clean.contains("oh ") -> OFFICE_HOURS
                clean.contains("внутреннее мероприятие") -> INTERNAL_EVENT
                clean.contains("другое") -> OTHER
                else -> UNKNOWN
            }
        }
    }
}
