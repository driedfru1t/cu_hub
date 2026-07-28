package com.nikol.lms.data.remote.model.material

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LongreadMaterialContentDto(
    @SerialName("name") val name: String,
    @SerialName("filename") val filename: String,
    @SerialName("mediaType") val mediaType: LongreadMaterialMediaTypeUpperDto,
    @SerialName("version") val version: String?,
    @SerialName("length") val length: Long
)

@Serializable
data class LongreadMaterialAttachmentItemDto(
    @SerialName("name") val name: String,
    @SerialName("filename") val filename: String,
    @SerialName("mediaType") val mediaType: FileMediaTypeDto,
    @SerialName("length") val length: Long,
    @SerialName("version") val version: String?
)

@Serializable
data class LongreadMaterialActivityDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("weight") val weight: Double,
    @SerialName("maxExercisesCount") val maxExercisesCount: Double,
    @SerialName("averageScoreThreshold") val averageScoreThreshold: Double?,
    @SerialName("isLateDaysEnabled") val isLateDaysEnabled: Boolean
)

@Serializable
data class LongreadMaterialEstimationDto(
    @SerialName("startDate") val startDate: String,
    @SerialName("timer") val timer: String?,
    @SerialName("maxScore") val maxScore: Double?,
    @SerialName("deadline") val deadline: String?,
    @SerialName("activity") val activity: LongreadMaterialActivityDto?
)

@Serializable
data class LongreadMaterialCodingDto(
    @SerialName("exerciseUrl") val exerciseUrl: String?
)

@Serializable
data class ExerciseQuestionsSettingsDto(
    @SerialName("questionsPerAttempt") val questionsPerAttempt: String?,
    @SerialName("attemptsLimit") val attemptsLimit: Int,
    @SerialName("evaluationStrategy") val evaluationStrategy: QuestionsSettingsEvalStrategyDto
)
