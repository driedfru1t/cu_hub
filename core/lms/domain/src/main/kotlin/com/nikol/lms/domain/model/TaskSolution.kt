package com.nikol.lms.domain.model

import kotlinx.serialization.json.JsonElement

data class TaskSolutionAnswersQuestionOptionsItem(
    val id: Int,
    val isCorrect: Boolean,
    val recommendation: String?
)

sealed interface TaskSolutionAnswersQuestion {
    val id: Int
}

data class TaskSolutionAnswersQuestionSingleChoice(
    override val id: Int,
    val answer: String?,
    val options: List<TaskSolutionAnswersQuestionOptionsItem>
) : TaskSolutionAnswersQuestion

data class TaskSolutionAnswersQuestionMultipleChoice(
    override val id: Int,
    val answer: String?,
    val options: List<TaskSolutionAnswersQuestionOptionsItem>
) : TaskSolutionAnswersQuestion

data class TaskSolutionAnswersQuestionInputAnswer(
    val values: List<String>,
    val value: String?,
    val recommendation: String?
)

data class TaskSolutionAnswersQuestionInput(
    override val id: Int,
    val answer: TaskSolutionAnswersQuestionInputAnswer?,
    val options: String?
) : TaskSolutionAnswersQuestion

data class TaskSolutionAnswersQuestionInputNumberAnswer(
    val values: List<Double>,
    val value: String?,
    val recommendation: String?
)

data class TaskSolutionAnswersQuestionInputNumber(
    override val id: Int,
    val answer: TaskSolutionAnswersQuestionInputNumberAnswer?,
    val options: String?
) : TaskSolutionAnswersQuestion

data class TaskSolutionAnswersItem(
    val answer: JsonElement, // Оставляем JsonElement в домене, так как тип ответа динамический (String или Double)
    val question: TaskSolutionAnswersQuestion
)

data class TaskSolution(
    val type: TaskType,
    val solutionUrl: String?,
    val answers: List<TaskSolutionAnswersItem>?,
    val attachments: List<TaskAttachment>?
)
