package com.nikol.calendar.domain.model

enum class Tower(
    val displayName: String,
    val shortCode: String,
    val maxFloors: Int?
) {
    BACKEND("Башня Backend", "B", maxFloors = 10),
    FRONTEND("Башня Frontend", "F", maxFloors = 4),
    EXTERNAL("Вне кампуса", "Ext", maxFloors = null),
    ONLINE("Онлайн", "Online", maxFloors = null),
    UNKNOWN("Неуказано", "?", maxFloors = null);

    companion object {
        fun fromRoomString(roomRaw: String): Tower {
            val upper = roomRaw.uppercase()
            return when {
                upper.startsWith("B") -> BACKEND
                upper.startsWith("F") -> FRONTEND
                upper.contains("АГАТ") || upper.contains("СЕТУНЬ") -> FRONTEND
                upper.contains("ТАГАНК") -> EXTERNAL
                upper.contains("ОНЛАЙН") || upper.contains("ONLINE") || upper.contains("HYBRID") -> ONLINE
                else -> UNKNOWN
            }
        }
    }
}