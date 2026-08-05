package com.nikol.calendar.domain.model

enum class EventBadgeColor(val emoji: String) {
    BLUE("🔵"),
    RED("🔴"),
    BLACK("⚫");

    companion object {
        fun extractColors(text: String): List<EventBadgeColor> {
            val result = mutableListOf<EventBadgeColor>()
            for (char in text) {
                when {
                    text.contains("🔵") -> result.add(BLUE) // обработка ниже через парсер
                }
            }
            return result
        }
    }
}