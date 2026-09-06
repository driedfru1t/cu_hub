package com.nikol.calendar.domain.model

sealed interface EventLocation {
    data class University(
        val campus: Campus,
        val building: CampusBuilding,
        val floor: Int?,
        val rooms: List<String>
    ) : EventLocation

    data object Online : EventLocation

    data class External(
        val description: String
    ) : EventLocation

    data object Unknown : EventLocation
}
