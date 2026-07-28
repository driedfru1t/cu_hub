package com.nikol.lms.data.remote.model.performance

import com.nikol.lms.data.remote.model.task.TaskStateDto
import com.nikol.lms.domain.model.TaskScoreSkillLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityStudentPerformanceDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("weight") val weight: Double,
    @SerialName("maxExercisesCount") val maxExercisesCount: Double,
    @SerialName("averageScoreThreshold") val averageScoreThreshold: Double?,
    @SerialName("isBlocker") val isBlocker: Boolean,
    @SerialName("bestScoresCount") val bestScoresCount: Double?
)

@Serializable
data class TaskStudentPerformanceDto(
    @SerialName("id") val id: Int,
    @SerialName("state") val state: TaskStateDto,
    @SerialName("score") val score: Double,
    @SerialName("scoreSkillLevel") val scoreSkillLevel: TaskScoreSkillLevel?,
    @SerialName("extraScore") val extraScore: Double?,
    @SerialName("exerciseId") val exerciseId: Int,
    @SerialName("maxScore") val maxScore: Double,
    @SerialName("activity") val activity: ActivityStudentPerformanceDto,
)

@Serializable
data class TaskStudentPerformanceItemsDto(
    @SerialName("tasks") val tasks: List<TaskStudentPerformanceDto>,
    @SerialName("total") val total: Double,
    @SerialName("blockerEnabled") val blockerEnabled: Boolean,
    @SerialName("courseBlockerTriggered") val courseBlockerTriggered: Boolean,
    @SerialName("activitiesBlockerTriggered") val activitiesBlockerTriggered: Boolean,
    @SerialName("blockerScore") val blockerScore: Double?
)