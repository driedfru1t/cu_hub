package com.nikol.lms.data.remote.model.task

import com.nikol.lms.data.remote.model.material.ExerciseQuestionsSettingsDto
import com.nikol.lms.data.remote.model.material.FileMediaTypeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskExerciseActivityDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("weight") val weight: Double,
    @SerialName("isLateDaysEnabled") val isLateDaysEnabled: Boolean
)

@Serializable
data class TaskExerciseQuestionOrderOptionOrderDto(
    @SerialName("optionId") val id: Int,
    @SerialName("order") val order: Int
)

@Serializable
data class TaskExerciseQuestionOrderDto(
    @SerialName("questionId") val questionId: Int,
    @SerialName("order") val order: Int,
    @SerialName("optionOrders") val optionOrders: List<TaskExerciseQuestionOrderOptionOrderDto>
)

@Serializable
data class TaskAttachmentDto(
    @SerialName("name") val name: String,
    @SerialName("filename") val filename: String,
    @SerialName("mediaType") val mediaType: FileMediaTypeDto,
    @SerialName("length") val length: Long,
    @SerialName("version") val version: String?
)

@Serializable
data class TaskExerciseDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("type") val type: TaskTypeDto,
    @SerialName("maxScore") val maxScore: Double,
    @SerialName("startDate") val startDate: String,
    @SerialName("deadline") val deadline: String,
    @SerialName("timer") val timer: String?,
    @SerialName("activity") val activity: TaskExerciseActivityDto,
    @SerialName("questionOrders") val questionOrders: List<TaskExerciseQuestionOrderDto>?,
    @SerialName("areQuestionsShuffled") val areQuestionsShuffled: Boolean?,
    @SerialName("quizId") val quizId: Int?,
    @SerialName("mode") val mode: String?,
    @SerialName("viewContent") val viewContent: String?,
    @SerialName("exerciseUrl") val exerciseUrl: String?,
    @SerialName("attachments") val attachments: List<TaskAttachmentDto>?,
    @SerialName("settings") val settings: ExerciseQuestionsSettingsDto?
)

@Serializable
data class TaskCourseDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("isArchived") val isArchived: Boolean
)

@Serializable
data class ShortCourseThemeDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

@Serializable
data class ShortLongreadDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

@Serializable
data class TaskReviewerDto(
    @SerialName("id") val id: String, // UUID
    @SerialName("name") val name: String,
    @SerialName("identityEmail") val identityEmail: String,
    @SerialName("email") val email: String,
    @SerialName("identityIdentifier") val identityIdentifier: String, // UUID
    @SerialName("timeAccount") val timeAccount: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("middleName") val middleName: String?
)

@Serializable
data class TaskScoresItemDto(
    @SerialName("questionId") val questionId: Int,
    @SerialName("score") val score: Double?
)
