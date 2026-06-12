package com.nikol.lms.data.remote.model.question

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class QuestionItemOptionDto(
    @SerialName("id") val id: Int,
    @SerialName("value") val value: String,
    @SerialName("order") val order: Int,
    @SerialName("isCorrect") val isCorrect: Boolean,
    @SerialName("recommendation") val recommendation: String?
)

@Serializable
data class SingleChoiceBlockDto(
    @SerialName("options") val options: List<QuestionItemOptionDto>
)

@Serializable
data class MultipleChoiceBlockDto(
    @SerialName("options") val options: List<QuestionItemOptionDto>
)

@Serializable
data class QuestionItemEvaluationBlockDto(
    @SerialName("correctAnswer") val correctAnswer: QuestionItemCorrectAnswerDto?,
    @SerialName("autoEvaluation") val autoEvaluation: Boolean,
    @SerialName("recommendation") val recommendation: String?
)
