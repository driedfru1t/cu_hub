package com.nikol.lms.data.remote.model.performance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityPerformanceDTO(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("weight") val weight: Double,
    @SerialName("maxExercisesCount") val maxExercisesCount: Double,
    @SerialName("isBlocker") val isBlocker: Boolean,
    @SerialName("bestScoresCount") val bestScoresCount: Double?

)

@Serializable
data class ActivityPerformanceItemDTO(
    @SerialName("activity") val activity: ActivityPerformanceDTO,
    @SerialName("total") val total: Double,
    @SerialName("average") val average: Double,
    @SerialName("blockerTriggered") val blockerTriggered: Boolean,
)

@Serializable
data class ActivityPerformanceItemsDTO(
    @SerialName("items") val items: List<ActivityPerformanceItemDTO>,
    @SerialName("blockerEnabled") val blockerEnabled: Boolean,
    @SerialName("blockerScore") val blockerScore: Double?,
    @SerialName("courseBlockerTriggered") val courseBlockerTriggered: Boolean,
    @SerialName("activitiesBlockerTriggered") val activitiesBlockerTriggered: Boolean,
    @SerialName("totalScore") val totalScore: Double,
    @SerialName("totalWeight") val totalWeight: Double
)
