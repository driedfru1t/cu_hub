package com.nikol.calendar.domain.model

data class RoomInfo(
    val raw: String,
    val rooms: List<String>,
    val tower: Tower,
    val floor: Int?,
    val isNamedRoom: Boolean = false,
    val isCombined: Boolean = rooms.size > 1,
    val isOnline: Boolean = tower == Tower.ONLINE
)