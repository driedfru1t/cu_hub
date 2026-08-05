package com.nikol.lms.data.remote.model.question

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface QuestionItemCorrectAnswerDto {
    val type: String
}

@Serializable
@SerialName("inputStringCorrectAnswer")
data class QuestionItemCorrectAnswerInputStringCorrectAnswerDto(
    @SerialName("type") override val type: String,
    @SerialName("variants") val variants: List<String>
) : com.nikol.lms.data.remote.model.question.QuestionItemCorrectAnswerDto

@Serializable
@SerialName("inputNumberCorrectAnswer")
data class QuestionItemCorrectAnswerInputNumberCorrectAnswerDto(
    @SerialName("type") override val type: String,
    @SerialName("variants") val variants: List<Double>,
    @SerialName("showPrecisionHint") val showPrecisionHint: Boolean,
    @SerialName("autoEvaluationPrecision") val autoEvaluationPrecision: String? = null,
    @SerialName("precision") val precision: Int?
) : com.nikol.lms.data.remote.model.question.QuestionItemCorrectAnswerDto
