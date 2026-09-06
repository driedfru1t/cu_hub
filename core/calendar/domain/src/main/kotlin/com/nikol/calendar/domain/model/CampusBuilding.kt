package com.nikol.calendar.domain.model

enum class CampusBuilding(
    val campus: Campus,
    val displayName: String,
    val shortCode: String,
    val maxFloors: Int?
) {
    // Дукат
    DUKAT_BACKEND(
        campus = Campus.DUKAT,
        displayName = "Башня B",
        shortCode = "B",
        maxFloors = 10
    ),

    DUKAT_FRONTEND(
        campus = Campus.DUKAT,
        displayName = "Башня F",
        shortCode = "F",
        maxFloors = 4
    ),

    // Центральный телеграф
    CT_WEST(
        campus = Campus.CENTRAL_TELEGRAPH,
        displayName = "Башня W",
        shortCode = "W",
        maxFloors = null
    ),

    CT_NORTH(
        campus = Campus.CENTRAL_TELEGRAPH,
        displayName = "Башня N",
        shortCode = "N",
        maxFloors = null
    ),

    CT_EAST(
        campus = Campus.CENTRAL_TELEGRAPH,
        displayName = "Башня E",
        shortCode = "E",
        maxFloors = null
    ),

    CT_SOUTH(
        campus = Campus.CENTRAL_TELEGRAPH,
        displayName = "Башня S",
        shortCode = "S",
        maxFloors = null
    ),

    UNKNOWN(
        campus = Campus.UNKNOWN,
        displayName = "Не указано",
        shortCode = "?",
        maxFloors = null
    );

    companion object {
        fun fromRoomString(rawStr: String): CampusBuilding {
            val upper = rawStr.uppercase()
            return when {
                upper.startsWith("W") -> CT_WEST
                upper.startsWith("N") -> CT_NORTH
                upper.startsWith("E") -> CT_EAST
                upper.startsWith("S") -> CT_SOUTH
                upper.startsWith("F") -> DUKAT_FRONTEND
                upper.startsWith("B") -> DUKAT_BACKEND
                upper.contains("АГАТ") || upper.contains("СЕТУНЬ") -> DUKAT_FRONTEND
                else -> UNKNOWN
            }
        }
    }
}