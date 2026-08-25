package com.nikol.ui.prewiewData

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nikol.lms.domain.model.ExerciseTaskSummary
import com.nikol.lms.domain.model.TaskCourse
import com.nikol.lms.domain.model.TaskCourseTheme
import com.nikol.lms.domain.model.TaskExerciseActivity
import com.nikol.lms.domain.model.TaskLongread
import com.nikol.lms.domain.model.TaskReviewer
import com.nikol.lms.domain.model.TaskScoreSkillLevel
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskSummary
import com.nikol.lms.domain.model.TaskType
import java.time.Instant

class TaskSummaryPreviewProvider : PreviewParameterProvider<TaskSummary> {
    override val values: Sequence<TaskSummary>
        get() = sequenceOf(
            TasksPreviewData.backlog,
            TasksPreviewData.inProgress,
            TasksPreviewData.submitted,
            TasksPreviewData.review,
            TasksPreviewData.evaluated,
            TasksPreviewData.failed,
            TasksPreviewData.reworking,
        )
}

object TasksPreviewData {

    private val createdAt = Instant.parse("2026-08-01T10:00:00Z")
    private val startDate = Instant.parse("2026-08-01T00:00:00Z")
    private val deadline = Instant.parse("2026-08-31T19:00:00Z")

    private val reviewer = TaskReviewer(
        id = "550e8400-e29b-41d4-a716-446655440000",
        name = "Иван Иванов",
        identityEmail = "ivan.ivanov@example.com",
        email = "ivan.ivanov@example.com",
        identityIdentifier = "550e8400-e29b-41d4-a716-446655440001",
        timeAccount = "Europe/Moscow",
        lastName = "Иванов",
        firstName = "Иван",
        middleName = "Иванович",
    )

    private fun task(
        id: Int,
        state: TaskState,
        name: String,
        score: Double? = null,
        scoreSkillLevel: TaskScoreSkillLevel? = null,
        startedAt: Instant? = null,
        submitAt: Instant? = null,
        rejectAt: Instant? = null,
        evaluateAt: Instant? = null,
        lateDays: Int? = null,
        reviewer: TaskReviewer? = null,
        extraScore: Double? = null,
    ) = TaskSummary(
        id = id,
        state = state,
        score = score,
        scoreSkillLevel = scoreSkillLevel,
        isLateDaysEnabled = lateDays != null,
        extraScore = extraScore,
        createdAt = createdAt,
        startedAt = startedAt,
        submitAt = submitAt,
        rejectAt = rejectAt,
        evaluateAt = evaluateAt,
        deadline = deadline,
        lateDays = lateDays,
        exercise = ExerciseTaskSummary(
            id = id + 1000,
            name = name,
            type = TaskType.CODING,
            maxScore = 10.0,
            startDate = startDate,
            deadline = deadline,
            activity = TaskExerciseActivity(
                id = 1119,
                name = "Задание",
                weight = 1.0,
                isLateDaysEnabled = false,
            ),
        ),
        course = TaskCourse(
            id = 492,
            name = "Нулевой семестр. Математика",
            isArchived = false,
        ),
        theme = TaskCourseTheme(
            id = id + 2000,
            name = name,
        ),
        longread = TaskLongread(
            id = id + 3000,
            name = "🏆 Домашнее задание",
        ),
        reviewer = reviewer,
        quizSessionId = null,
    )

    val backlog = task(
        id = 1,
        state = TaskState.BACKLOG,
        name = "Кванторы и логика",
    )

    val inProgress = task(
        id = 2,
        state = TaskState.IN_PROGRESS,
        name = "Многочлены",
        startedAt = Instant.parse("2026-08-20T14:30:00Z"),
    )

    val submitted = task(
        id = 3,
        state = TaskState.SUBMITTED,
        name = "Матрицы",
        startedAt = Instant.parse("2026-08-20T12:00:00Z"),
        submitAt = Instant.parse("2026-08-20T15:45:00Z"),
    )

    val review = task(
        id = 4,
        state = TaskState.REVIEW,
        name = "Суммирование",
        score = 7.0,
        scoreSkillLevel = TaskScoreSkillLevel.INTERMEDIATE,
        startedAt = Instant.parse("2026-08-19T10:00:00Z"),
        submitAt = Instant.parse("2026-08-19T16:30:00Z"),
        reviewer = reviewer
    )

    val evaluated = task(
        id = 5,
        state = TaskState.EVALUATED,
        name = "Доказательства",
        score = 9.5,
        scoreSkillLevel = TaskScoreSkillLevel.ADVANCED,
        extraScore = 0.5,
        startedAt = Instant.parse("2026-08-18T09:00:00Z"),
        submitAt = Instant.parse("2026-08-18T13:00:00Z"),
        evaluateAt = Instant.parse("2026-08-18T16:00:00Z"),
        reviewer = reviewer
    )

    val failed = task(
        id = 6,
        state = TaskState.FAILED,
        name = "Суммирование",
        score = 0.0,
        startedAt = Instant.parse("2026-08-20T13:58:37Z"),
        rejectAt = Instant.parse("2026-08-20T15:20:00Z"),
    )

    val reworking = task(
        id = 7,
        state = TaskState.REWORKING,
        name = "Многочлены",
        score = 4.0,
        scoreSkillLevel = TaskScoreSkillLevel.BASIC,
        startedAt = Instant.parse("2026-08-17T10:00:00Z"),
        submitAt = Instant.parse("2026-08-17T14:00:00Z"),
        rejectAt = Instant.parse("2026-08-17T16:00:00Z"),
        reviewer = reviewer
    )
}