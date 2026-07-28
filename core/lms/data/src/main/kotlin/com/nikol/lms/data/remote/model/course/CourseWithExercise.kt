package com.nikol.lms.data.remote.model.course

import com.nikol.lms.data.remote.model.task.ShortLongreadDto
import com.nikol.lms.data.remote.model.task.TaskTypeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseWithExercisesResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("isArchived") val isArchived: Boolean,
    @SerialName("settings") val settings: CourseSettingsDTO,
    @SerialName("blocker") val blocker: BlockerDTO,
    @SerialName("exercises") val exercises: List<CourseExerciseItemDto>
)


@Serializable
data class CourseExerciseItemDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("type") val type: TaskTypeDto,
    @SerialName("activity") val activity: CourseExerciseActivityDto,
    @SerialName("longread") val longread: ShortLongreadDto,
    @SerialName("theme") val theme: CourseExerciseThemeDto
)

@Serializable
data class CourseExerciseActivityDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("bestScoresCount") val bestScoresCount: Double?,
    @SerialName("isBlocker") val isBlocker: Boolean
)

@Serializable
data class CourseExerciseThemeDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("order") val order: Int
)
