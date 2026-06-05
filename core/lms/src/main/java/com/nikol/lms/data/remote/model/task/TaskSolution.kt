package com.nikol.lms.data.remote.model.task

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement

@Serializable
data class TaskSolutionAnswersQuestionOptionsItemDto(
    @SerialName("id") val id: Int,
    @SerialName("isCorrect") val isCorrect: Boolean,
    @SerialName("recommendation") val recommendation: String?
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface TaskSolutionAnswersQuestionDto {
    val id: Int
    val type: String
}

@Serializable
@SerialName("singleChoice")
data class TaskSolutionAnswersQuestionDtoSingleChoiceDto(
    @SerialName("id") override val id: Int,
    @SerialName("type") override val type: String,
    @SerialName("answer") val answer: String? = null,
    @SerialName("options") val options: List<TaskSolutionAnswersQuestionOptionsItemDto>
) : TaskSolutionAnswersQuestionDto

@Serializable
@SerialName("multipleChoice")
data class TaskSolutionAnswersQuestionDtoMultipleChoiceDto(
    @SerialName("id") override val id: Int,
    @SerialName("type") override val type: String,
    @SerialName("answer") val answer: String? = null,
    @SerialName("options") val options: List<TaskSolutionAnswersQuestionOptionsItemDto>
) : TaskSolutionAnswersQuestionDto

@Serializable
data class TaskSolutionAnswersQuestionInputAnswerDto(
    @SerialName("values") val values: List<String>,
    @SerialName("value") val value: String?,
    @SerialName("recommendation") val recommendation: String?
)

@Serializable
@SerialName("input")
data class TaskSolutionAnswersQuestionInputQuestionDtoItemDto(
    @SerialName("id") override val id: Int,
    @SerialName("type") override val type: String,
    @SerialName("answer") val answer: TaskSolutionAnswersQuestionInputAnswerDto?,
    @SerialName("options") val options: String? = null
) : TaskSolutionAnswersQuestionDto

@Serializable
data class TaskSolutionAnswersQuestionInputNumberAnswerDto(
    @SerialName("values") val values: List<Double>,
    @SerialName("value") val value: String?,
    @SerialName("recommendation") val recommendation: String?
)

@Serializable
@SerialName("inputNumber")
data class TaskSolutionAnswersQuestionInputNumberQuestionDtoItem(
    @SerialName("id") override val id: Int,
    @SerialName("type") override val type: String,
    @SerialName("answer") val answer: TaskSolutionAnswersQuestionInputNumberAnswerDto?,
    @SerialName("options") val options: String? = null
) : TaskSolutionAnswersQuestionDto

@Serializable
data class TaskSolutionAnswersItem(
    @SerialName("answer") val answer: JsonElement, // Может парситься в String или Double в рантайме
    @SerialName("question") val question: TaskSolutionAnswersQuestionDto
)

@Serializable
data class TaskSolution(
    @SerialName("type") val type: TaskTypeDto,
    @SerialName("solutionUrl") val solutionUrl: String?,
    @SerialName("answers") val answers: List<TaskSolutionAnswersItem>?,
    @SerialName("attachments") val attachments: List<TaskAttachmentDto>?
)
