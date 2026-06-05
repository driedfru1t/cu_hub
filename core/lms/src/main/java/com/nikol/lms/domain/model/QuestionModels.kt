package com.nikol.lms.domain.model

data class QuestionOption(
    val id: Int,
    val value: String,
    val order: Int,
    val isCorrect: Boolean,
    val recommendation: String?
)

data class SingleChoiceBlock(
    val options: List<QuestionOption>
)

data class MultipleChoiceBlock(
    val options: List<QuestionOption>
)

data class EvaluationBlock(
    val correctAnswer: CorrectAnswer?,
    val autoEvaluation: Boolean,
    val recommendation: String?
)

sealed interface CorrectAnswer

data class StringCorrectAnswer(
    val variants: List<String>
) : CorrectAnswer

data class NumberCorrectAnswer(
    val variants: List<Double>,
    val showPrecisionHint: Boolean,
    val autoEvaluationPrecision: String?,
    val precision: Int?
) : CorrectAnswer

sealed interface QuestionItem {
    val id: Int
    val order: Int
    val content: String
    val score: Double
    val attachments: List<MaterialAttachment>
}

data class InputQuestion(
    override val id: Int,
    override val order: Int,
    override val content: String,
    override val score: Double,
    override val attachments: List<MaterialAttachment>,
    val correctAnswer: CorrectAnswer?,
    val autoEvaluation: Boolean,
    val recommendation: String?,
    val input: EvaluationBlock
) : QuestionItem

data class SingleChoiceQuestion(
    override val id: Int,
    override val order: Int,
    override val content: String,
    override val score: Double,
    override val attachments: List<MaterialAttachment>,
    val options: List<QuestionOption>,
    val areOptionsShuffled: Boolean,
    val singleChoice: SingleChoiceBlock
) : QuestionItem

data class MultipleChoiceQuestion(
    override val id: Int,
    override val order: Int,
    override val content: String,
    override val score: Double,
    override val attachments: List<MaterialAttachment>,
    val options: List<QuestionOption>,
    val areOptionsShuffled: Boolean,
    val multipleChoice: MultipleChoiceBlock
) : QuestionItem
