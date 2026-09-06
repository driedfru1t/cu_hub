package com.nikol.calendar.domain.model


enum class Campus {
    CENTRAL_TELEGRAPH,
    DUKAT,
    UNKNOWN;

    companion object {
        fun fromRoomString(rawStr: String): Campus {
            val upper = rawStr.uppercase()
            return when {
                upper.contains("ЦТ") -> CENTRAL_TELEGRAPH
                upper.contains("ДУКАТ") -> DUKAT
                else -> UNKNOWN
            }
        }
    }
}