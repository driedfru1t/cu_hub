package com.nikol.lms.data.remote.model.question

import com.nikol.lms.data.remote.model.material.LongreadMaterialAttachmentItemDto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface LongreadExerciseQuestionItemDto {
    val type: String
    val id: Int
    val order: Int
    val content: String // JSON string в формате '{"description": "..."}'
    val score: Double
    val attachments: List<LongreadMaterialAttachmentItemDto>
}

@Serializable
@SerialName("input")
data class LongreadExerciseInputQuestionItemDto(
    @SerialName("type") override val type: String,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("content") override val content: String,
    @SerialName("score") override val score: Double,
    @SerialName("attachments") override val attachments: List<LongreadMaterialAttachmentItemDto>,
    @SerialName("correctAnswer") val correctAnswer: com.nikol.lms.data.remote.model.question.QuestionItemCorrectAnswerDto?,
    @SerialName("autoEvaluation") val autoEvaluation: Boolean,
    @SerialName("recommendation") val recommendation: String?,
    @SerialName("input") val input: com.nikol.lms.data.remote.model.question.QuestionItemEvaluationBlockDto
) : com.nikol.lms.data.remote.model.question.LongreadExerciseQuestionItemDto

@Serializable
@SerialName("singleChoice")
data class LongreadExerciseSingleChoiceQuestionitemDto(
    @SerialName("type") override val type: String,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("content") override val content: String,
    @SerialName("score") override val score: Double,
    @SerialName("attachments") override val attachments: List<LongreadMaterialAttachmentItemDto>,
    @SerialName("options") val options: List<com.nikol.lms.data.remote.model.question.QuestionItemOptionDto>,
    @SerialName("areOptionsShuffled") val areOptionsShuffled: Boolean,
    @SerialName("singleChoice") val singleChoice: com.nikol.lms.data.remote.model.question.SingleChoiceBlockDto
) : com.nikol.lms.data.remote.model.question.LongreadExerciseQuestionItemDto

@Serializable
@SerialName("multipleChoice")
data class LongreadExerciseMultipleChoiceQuestionItemDto(
    @SerialName("type") override val type: String,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("content") override val content: String,
    @SerialName("score") override val score: Double,
    @SerialName("attachments") override val attachments: List<LongreadMaterialAttachmentItemDto>,
    @SerialName("options") val options: List<com.nikol.lms.data.remote.model.question.QuestionItemOptionDto>,
    @SerialName("areOptionsShuffled") val areOptionsShuffled: Boolean,
    @SerialName("multipleChoice") val multipleChoice: com.nikol.lms.data.remote.model.question.MultipleChoiceBlockDto
) : com.nikol.lms.data.remote.model.question.LongreadExerciseQuestionItemDto