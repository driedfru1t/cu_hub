package com.nikol.lms.domain.model

import com.nikol.lms.domain.common.UnstableLmsApi
import kotlinx.serialization.SerialName
import java.time.Duration
import java.time.Instant

enum class TaskType {
    CODING, QUESTIONS
}

enum class TaskState {
    BACKLOG, IN_PROGRESS, SUBMITTED, REVIEW, EVALUATED, FAILED, REWORKING
}

enum class TaskScoreSkillLevel {
    NONE, BASIC, INTERMEDIATE, ADVANCED
}
data class TaskSummary(
    val id: Int,
    val state: TaskState,
    val score: Double?,
    val scoreSkillLevel: TaskScoreSkillLevel?,
    val isLateDaysEnabled: Boolean,
    val extraScore: Double?,
    val createdAt: Instant,
    val startedAt: Instant?,
    val submitAt: Instant?,
    val rejectAt: Instant?,
    val evaluateAt: Instant?,
    val deadline: Instant,
    val lateDays: Int?,
    val exercise: ExerciseTaskSummary,
    val course: TaskCourse,
    val theme: TaskCourseTheme,
    val longread: TaskLongread,
    val reviewer: TaskReviewer?,
    val quizSessionId: Int?,
)

data class ExerciseTaskSummary(
    val id: Int,
    val name: String,
    val type: TaskType,
    val maxScore: Double,
    val startDate: Instant,
    val deadline: Instant,
    val activity: TaskExerciseActivity
)

data class TaskExerciseActivity(
    val id: Int,
    val name: String,
    val weight: Double,
    val isLateDaysEnabled: Boolean
)

data class TaskExerciseQuestionOrderOptionOrder(
    val optionId: Int,
    val order: Int
)

data class TaskExerciseQuestionOrder(
    val questionId: Int,
    val order: Int,
    val optionOrders: List<TaskExerciseQuestionOrderOptionOrder>
)

data class TaskAttachment(
    val name: String,
    val filename: String,
    val mediaType: FileMediaType,
    val length: Long,
    val version: String?
)

data class TaskExercise(
    val id: Int,
    val name: String,
    val type: TaskType,
    val maxScore: Double,
    val startDate: Instant,
    val deadline: Instant,
    val timer: Duration?,
    val activity: TaskExerciseActivity,
    val questionOrders: List<TaskExerciseQuestionOrder>?,
    val areQuestionsShuffled: Boolean?,
    val quizId: Int?,
    val mode: String?,
    val viewContent: String?,
    val exerciseUrl: String?,
    val attachments: List<TaskAttachment>?,
    val settings: QuestionsSettings?
)

data class TaskCourse(
    val id: Int,
    val name: String,
    val isArchived: Boolean
)

data class TaskCourseTheme(
    val id: Int,
    val name: String
)

data class TaskLongread(
    val id: Int,
    val name: String
)

data class TaskReviewer(
    val id: String, // UUID
    val name: String,
    val identityEmail: String,
    val email: String,
    val identityIdentifier: String, // UUID
    val timeAccount: String,
    val lastName: String,
    val firstName: String,
    val middleName: String?
)

data class TaskScoresItem(
    val questionId: Int,
    val score: Double?
)

data class TaskDetails(
    val id: Int,
    val type: TaskType,
    val state: TaskState,
    val score: Double?,
    val scoreSkillLevel: TaskScoreSkillLevel?,
    val isSkillLevelEnabled: Boolean,
    val isLateDaysEnabled: Boolean,
    val extraScore: Double?,
    val createdAt: Instant,
    val startedAt: Instant?,
    val submitAt: Instant?,
    val rejectAt: Instant?,
    val evaluateAt: Instant?,
    val deadline: Instant,
    val lateDays: Int?,
    val exercise: TaskExercise,
    val course: TaskCourse,
    val theme: TaskCourseTheme,
    val longread: TaskLongread,
    val student: LmsProfile,
    val reviewer: TaskReviewer?,
    val solution: TaskSolution?,
    val scores: List<TaskScoresItem>?,
    val quizSessionId: Int?,
    val evaluatedAttemptId: Int?,
    val currentAttemptId: Int?,
    val lastAttemptId: Int?
)

// Вспомогательный класс-нагрузка для отправки решения
data class TaskAttachmentPayload(
    val name: String,
    val filename: String,
    val mediaType: FileMediaType,
    val length: Long,
    val version: String?
)
